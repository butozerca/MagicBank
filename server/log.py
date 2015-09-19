import sys

class Log:
    @staticmethod
    def debug(data):
        sys.stderr.write(data + "\n")
