import socket
import time
import keyboard as key



class enum:
    pushed = 0
    unpushed = 1

def main():
    socket = javaSocket()
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
        while True:
            a = int(input())
            s.sendState(a)

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