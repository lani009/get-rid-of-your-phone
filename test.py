import socket
import time
import keyboard as key


class enum:
    pushed = 0
    unpushed = 1

def main():
    btnDet = button()
    btnDet.stateSender(socket)

class button:
    originTime = None
    originState = None
    isOrigin = True
    originData = 1
    alerted = False
    def __init(self):
        pass

    def stateSender(self, s):
        self.originState = enum.unpushed
        while True:
            inputIO = key.is_pressed("k")
            if(inputIO):
                inputIO = 0
            else:
                inputIO = 1
            if(self.isOrigin and inputIO == self.originState):
                self.originTime = time.time()
                self.isOrigin = False
            elif(inputIO == self.originState):
                if(self.isTimeover(self.originTime)):
                    if(inputIO):
                        data = 0
                    else:
                        data = 1

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


class javaSocket:
    def __init__(self):
        self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.s.connect(("localhost", 8887))
        print("connect success")

    def sendState(self, data):
        byte = bytearray()
        byte.append(data)
        self.s.send(byte)

if __name__ == "__main__":
    main()