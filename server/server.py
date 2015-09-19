from twisted.internet import reactor

from protocol import BrutalBankFactory

reactor.listenTCP(4325, BrutalBankFactory())
reactor.run()
