import socket
import time
import RPi.GPIO as GPIO

GPIO.setmode(GPIO.BCM)

GPIO.setup(21, GPIO.IN)

class enum:
    pushed = 0
    unpushed = 1

def main():
    btnDet = button()
    btnDet.stateSender()

class button:
    originTime = None
    originState = None
    isOrigin = True
    originData = 1
    alerted = False
    def __init(self):
        pass

    def stateSender(self):
        self.originState = enum.unpushed
        while True:
            inputIO = GPIO.input(21)
            if(self.isOrigin and inputIO == self.originState):
                self.originTime = time.time()
                self.isOrigin = False
            elif(inputIO == self.originState):
                if(self.isTimeover(self.originTime)):
                    if(inputIO):
                        data = 1
                    else:
                        data = 2

                    if(self.originData != data):
                        self.alerted = False
                        self.originData = data

                    if(not self.alerted):
                        print(data)
                        self.alerted = True
            else:
                self.originState = inputIO
                self.isOrigin = True

            time.sleep(0.02)

    def isTimeover(self, originTime):
        if(time.time() - originTime > 3.0):
            return True
        else:
            return False

if __name__ == "__main__":
    main()