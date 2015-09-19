from twisted.internet import reactor
from twisted.web.server import Site

from protocol import BrutalBankProtocol

site = Site(BrutalBankProtocol())
reactor.listenTCP(4325, site)
reactor.run()
