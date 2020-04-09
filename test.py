import socket
import time



class enum:
    pushed = 0
    unpushed = 1

def main():
    socket = javaSocket()
    while True:
        socket.sendState(int(input()))


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