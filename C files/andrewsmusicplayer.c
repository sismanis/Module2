#include <string.h>
#include <stdbool.h>
#include "system.h"
#include <stdio.h>
#include <stdlib.h>
#include <altera_up_sd_card_avalon_interface.h>
#include <assert.h>
#include <altera_up_avalon_audio_and_video_config.h>
#include <altera_up_avalon_audio.h>
#include <sys/alt_irq.h>


typedef struct Wave {
	char* filename;
	int datasize;
	unsigned short channels;
	unsigned int samplerate;
	unsigned short samplesize;
	unsigned char* songData;
} Wave;

void audio_configs_setup(void);
Wave* isWav(char* filename);
int file_read(char* charbuffer, char* filename, int charmax);
void makesound();
void setactivesound(char* filename);
void audio_isr(void * context, unsigned int irq_id);


unsigned char *soundbuffer;
int songsize;
const int bufferconst = 96;
alt_up_audio_dev* audio;
#define WAV_OFFSET 44
Wave* newwav;

unsigned int * song1;
unsigned int * song2;

unsigned int* active_sound = NULL;
int sound_marker = 0;

int playing;

void opensd();

int printplaying = 0;
char songlist[20][20];
bool song_playing = false;
bool wavecheck(char* filename);
int songquantity;
void playsongnum(int songid);
void printqueue();

int main(void) {

//char songlist2[20][50];


	alt_irq_register(AUDIO_0_IRQ, NULL, audio_isr);
	alt_irq_enable(AUDIO_0_IRQ);
	alt_up_sd_card_dev* device_sd = NULL;
	device_sd = alt_up_sd_card_open_dev("/dev/sd_card");
	audio_configs_setup();
	opensd();

	printqueue();

int songnum;
while(1){
printf("Enter a song number: (1 to %d)", songquantity);
scanf("%d",&songnum);
while(song_playing == true){}
playsongnum(songnum);
}
printf("done");

return 0;

}
void printqueue(){
int test = 0;
for(test = 0; test <20; test++){
	if(songlist[test][0] != '\0' ){
		printf("%d \t %s \n", (test+1), songlist[test]);
		songquantity = test+1;}
}
}
void playsongnum(int songid){


while(1){
		if(song_playing == false){
			if(songid <= songquantity){
				setactivesound(songlist[songid-1]);
				alt_up_audio_enable_write_interrupt(audio);
				printplaying = 0;
			}
		}
		else if(song_playing == true && printplaying == 0){
					printf("playing...\n");
					printplaying = 1;
					break;
		}
	}
}

void setactivesound(char* filename){
	int j;
	newwav = isWav(filename);
	if (active_sound)
		free(active_sound);
	active_sound = malloc((newwav->datasize / 2) * sizeof(unsigned int));
	assert(active_sound);
	for (j = 0; j < (newwav->datasize); j += 2) {
		active_sound[j / 2] = (newwav->songData[j + 1] << 8) | newwav->songData[j];
		//printf("%x\t%x\t%d\n", newwav->songData[j], newwav->songData[j+1], d[j]);
}
	song_playing = true;
}

int file_read(char* charbuffer, char* filename, int charmax) {
if ((alt_up_sd_card_is_Present())) {
	if (alt_up_sd_card_is_FAT16()) {
		short int file0;
		int k;
		int charcount;
		file0 = alt_up_sd_card_fopen(filename, false);
		if (file0 == -1)
			return -1;
		if (file0 == -2)
			return -2;
		else {
			for (k = 0; k < charmax; k++) {
				short data = alt_up_sd_card_read(file0);
				if (data == -1)
					break;
				else {
					*(charbuffer + k) = (char) data;
				}
				charcount = k;
			}
		}
		alt_up_sd_card_fclose(file0);
		return charcount;
	}
}
return -1;

}

bool wavecheck(char* filename){
	char fileheader[WAV_OFFSET + 1];
	char* fileheaderptr = &fileheader[0];
	if (file_read(fileheaderptr, filename, WAV_OFFSET) < 0) {
		return false;
	}
	if ((fileheader[8] == 'W') && fileheader[9] == 'A' && fileheader[10] == 'V'
			&& fileheader[11] == 'E') {
		return true;
	}
	else
		return false;

}
Wave* isWav(char* filename) {
char fileheader[WAV_OFFSET + 1];
char* fileheaderptr = &fileheader[0];
Wave* File;
unsigned char* currentSong;
//free(File);
//free(currentSong);

if (file_read(fileheaderptr, filename, WAV_OFFSET) < 0) {
	return NULL;
}
if ((fileheader[8] == 'W') && fileheader[9] == 'A' && fileheader[10] == 'V'
		&& fileheader[11] == 'E') {
	File = malloc(sizeof(Wave));
	assert(File);
	if (File == NULL)
		return NULL;
	File->datasize = (unsigned char) fileheader[40]
			+ (unsigned char) fileheader[41] * 256
			+ (unsigned char) fileheader[42] * 65536;
	File->channels = fileheader[22];
	File->samplerate = (unsigned char) fileheader[24]
			+ (unsigned char) fileheader[25] * 256
			+ (unsigned char) fileheader[26] * 65536;
	File->samplesize = fileheader[34];
	File->filename = filename;
	currentSong = malloc((File->datasize + WAV_OFFSET + 1)* sizeof(char));
	assert(currentSong);
	if (File == NULL) {
		printf("Couldn't allocate memory to note %s\n", filename);
		return File;
	}
	file_read(currentSong, File->filename, File->datasize + WAV_OFFSET);
	File->songData = currentSong;
	return File;
} else
	return NULL;
}

void audio_isr(void * context, unsigned int irq_id) {
	//		while (alt_up_audio_write_fifo_space(audio, ALT_UP_AUDIO_LEFT) < 92) {
	//		}

	alt_up_audio_write_fifo(audio, &active_sound[sound_marker], 92, ALT_UP_AUDIO_LEFT);
	alt_up_audio_write_fifo(audio, &active_sound[sound_marker], 92, ALT_UP_AUDIO_RIGHT);
	sound_marker = sound_marker + 92;
	//printf("Z: %d\n", z);

	if (sound_marker > (newwav->datasize / 2)) {
		sound_marker = 0;
		song_playing = false;
		alt_up_audio_disable_write_interrupt(audio);

	}

}
void audio_configs_setup(void) {
alt_up_av_config_dev * av_config = alt_up_av_config_open_dev(
		AUDIO_AND_VIDEO_CONFIG_0_NAME);
while (!alt_up_av_config_read_ready(av_config)) {
}
audio = alt_up_audio_open_dev(AUDIO_0_NAME);
alt_up_audio_reset_audio_core(audio);
}


void opensd() {
    alt_up_sd_card_dev* sdcard;
    char* directory = ".";
    char* filename;
    short next_file = 0;
    int songid = 0;
    short first_file;
    char* next_filename;

    sdcard = alt_up_sd_card_open_dev("/dev/sd_card");
        if (alt_up_sd_card_is_Present()) {
            if (alt_up_sd_card_is_FAT16()) {
                first_file = alt_up_sd_card_find_first(directory, filename);
                    while (next_file == 0) {

                        next_file = alt_up_sd_card_find_next(next_filename);
                        if (next_file == 0){
                        	if(wavecheck(next_filename) == true){
                        		strcpy(songlist[songid], next_filename);
                        		//printf("%s \t %d \n", songlist[songid], songid);
                        		songid++;
                        	}
                        }
                    }
            }
        }

}


