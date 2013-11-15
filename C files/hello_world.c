#include <stdio.h>
#include "altera_up_avalon_rs232.h"
#include <string.h>
#include "system.h"
#include <stdbool.h>

char songlist[5][5];
bool stay;



int main()
{
	strcpy(songlist[0], "song1");
	strcpy(songlist[1], "song2");
	strcpy(songlist[2], "song3");
	strcpy(songlist[3], "song4");
	strcpy(songlist[4], "song5");

 int i;
 int j = 0;
 unsigned char data;
 unsigned char parity;
 int songnum = 0;

 printf("UART Initialization\n");
 alt_up_rs232_dev* uart = alt_up_rs232_open_dev(RS232_0_NAME);

 printf("Clearing read buffer to start\n");
 while (alt_up_rs232_get_used_space_in_read_FIFO(uart)) {
 alt_up_rs232_read_data(uart, &data, &parity);
 }

 //printf("Sending the message to the Middleman\n");

 // Start with the number of bytes in our message

// alt_up_rs232_write_data(uart, (unsigned char) strlen(message));

 // Now send the actual message to the Middleman

 //for (i = 0; i < strlen(message); i++) {
// alt_up_rs232_write_data(uart, message[i]);
// }

 // Now receive the message from the Middleman

 /*printf("Waiting for data to come from the Middleman\n");
 while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0);

 // First byte is the number of characters in our message

 alt_up_rs232_read_data(uart, &data, &parity);
 int num_to_receive = (int)data;
 unsigned char message[num_to_receive];
 printf("About to receive %d characters:\n", num_to_receive);

 for (i = 0; i < num_to_receive; i++) {
 while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0)
 ;
 alt_up_rs232_read_data(uart, &data, &parity);
message[i] = data;
 //printf("%c", data);
 }*/
for(songnum = 0; songnum <5; songnum++){
 printf("Sending song %d to the Middleman\n", songnum);

  // Start with the number of bytes in our message

  //alt_up_rs232_write_data(uart, (unsigned char) "5");

  // Now send the actual message to the Middleman

  for (i = 0; i < 5; i++) {
  alt_up_rs232_write_data(uart, songlist[songnum][i]);
  }
 // while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0);
  stay = 1;
  while(stay == 1) {
   while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0);
   alt_up_rs232_read_data(uart, &data, &parity);
   if( data == 0xF)
	   stay = 0;
  }
}
 printf("\n");
 printf("Message Echo Complete\n");

 return 0;
}
