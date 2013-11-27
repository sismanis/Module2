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

//Function Statements-----------------------------------
void audio_configs_setup(void);
void pause();
void resume();
void stop();
void songfinished();
void audio_isr(void * context, unsigned int irq_id);
void startsong(char* filename);
void opensd();
void play(char* filename);
void getnextsongdata();
void read_songinfo(void);
void main_player_function(int inputnum);
void restart_song();
bool wavecheck(char* filename);
void printqueue();
//----------------------------------------------------

//Global Varaibles----
alt_up_audio_dev* audio;
#define WAV_OFFSET 44
int songsize;
unsigned int active_sound [92];
int sound_marker = 0;
int playing;
int soundmarker = 0;
char songlist[50][50];
bool song_playing = false;
bool interruptsong = false;
int songquantity;
int songsize;
short int filehandle;
int previous_song = -1;
int playing;
int samples_avail;
//-----------------------

int main(void) {

//Initialization Statements---------------------------
	alt_irq_register(AUDIO_0_IRQ, NULL, audio_isr);
	alt_irq_enable(AUDIO_0_IRQ);
	alt_up_sd_card_dev* device_sd = NULL;
	device_sd = alt_up_sd_card_open_dev("/dev/sd_card");
	audio_configs_setup();
	alt_up_audio_disable_write_interrupt(audio);
	opensd();
	play("startup1.wav");
//----------------------------------------------------


	 printqueue();
	 int songnum;
	 while(1){
	 printf("Enter a song number: (1 to %d)", songquantity);
	 scanf("%d",&songnum);
	 main_player_function(songnum);
	 }
	printf("done");

	return 0;

}
void main_player_function(int inputnum){
	 if(inputnum == -1){
		 if(song_playing == true)
			 pause();
	 }
	 else if(inputnum == -2){
		 if(song_playing == true)
			 resume();
	 }
	 else if(inputnum == -3){
		 stop();
	 }
	 else if(inputnum == -4){
		 interruptsong = true;
	 }
	 else if(inputnum == -5){
		 if(previous_song > 0){
		 interruptsong = true;
		 play(songlist[previous_song]);
		 interruptsong = false;
		 }
	 }
	 else if(inputnum <= songquantity){
		 	play(songlist[inputnum-1]);
		 	interruptsong = false;
		 	previous_song = inputnum-1;
	 }
}

void printqueue() {
	int test = 0;
	for (test = 0; test < 50; test++) {
		if (songlist[test][0] != '\0') {
			printf("%d \t %s \n", (test + 1), songlist[test]);
			songquantity = test + 1;
		}
	}
}

void play(char* filename) {
	if(interruptsong == true){
	if(song_playing == true){
		stop();
		song_playing = false;
	}
	}
	if(song_playing == false){
	//songsize = startsong(filename);
	startsong(filename);
	getnextsongdata();
	alt_up_audio_enable_write_interrupt(audio);
	song_playing = true;
	}

}
void startsong(char* filename) {
	filehandle = alt_up_sd_card_fopen(filename, false);
}

bool wavecheck(char* filename) {
	char fileheader[WAV_OFFSET + 1];
	char* fileheaderptr = &fileheader[0];
	if (file_read(fileheaderptr, filename, WAV_OFFSET) < 0) {
		return false;
	}
	if ((fileheader[8] == 'W') && fileheader[9] == 'A' && fileheader[10] == 'V'
			&& fileheader[11] == 'E') {
		return true;
	} else
		return false;
}
void song_finished() {
	alt_up_sd_card_fclose(filehandle);
}

void stop() {
	alt_up_audio_disable_write_interrupt(audio);
	song_finished();
	song_playing = false;
}

void pause() {
	alt_up_audio_disable_write_interrupt(audio);

}

void resume() {
	alt_up_audio_enable_write_interrupt(audio);
}

void restart_song(int songplaying){
	stop();
	 while(song_playing == true){}
	 	 play(songlist[songplaying-1]);

}

void audio_isr(void * context, unsigned int irq_id) {
	alt_up_audio_write_fifo(audio, active_sound, samples_avail,
			ALT_UP_AUDIO_LEFT);
	alt_up_audio_write_fifo(audio, active_sound, samples_avail,
			ALT_UP_AUDIO_RIGHT);

	if (samples_avail < 92){
		alt_up_audio_disable_write_interrupt(audio);
		song_playing = false;
		song_finished();
	} else {
		getnextsongdata();
	}
	return;
}
void read_songinfo(void){

}

void getnextsongdata() {

	int k;
	int j;
	unsigned char songbits[184];

	for (k = 0; k < 184; k++) {
		short data = alt_up_sd_card_read(filehandle);
		if (data == -1)
			break;
		else {
			songbits[k] = (char) data;
		}
	}
	for (j = 0; j < k; j += 2) {
		active_sound[j / 2] = (songbits[j + 1] << 8) | songbits[j];
	}
	samples_avail = k/2;
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

void opensd() {
	alt_up_sd_card_dev* sdcard;
	char directory = ".";
	char filename;
	short next_file = 0;
	int songid = 0;
	short first_file;
	char next_filename;

	sdcard = alt_up_sd_card_open_dev("/dev/sd_card");
	if (alt_up_sd_card_is_Present()) {
		if (alt_up_sd_card_is_FAT16()) {
			first_file = alt_up_sd_card_find_first(directory, filename);
			while (next_file == 0) {

				next_file = alt_up_sd_card_find_next(next_filename);
				if (next_file == 0) {
					if (wavecheck(next_filename) == true) {
						strcpy(songlist[songid], next_filename);
						//printf("%s \t %d \n", songlist[songid], songid);
						songid++;
					}
				}
			}
		}
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

