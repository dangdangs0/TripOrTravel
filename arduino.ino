#include <LiquidCrystal.h>
LiquidCrystal lcd(12, 11, 5, 4, 3, 2);
String inData;

void setup() {
  Serial.begin(9600);       
    lcd.begin(16, 2);
    lcd.print("Welcome!");
    delay(3000);
    lcd.clear();
}
void loop() {
 int i=0;
  char commandbuffer[100];

  if(Serial.available()){
     delay(100);
     while( Serial.available() && i< 99) {
        commandbuffer[i++] = Serial.read();
     }
     commandbuffer[i++]='\0';
  }

  if(i>0)

     Serial.println((char*)commandbuffer);
     lcd.print((char*)commandbuffer);
     delay(1000);
     lcd.clear();
}
