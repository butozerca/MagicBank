from service import Service


class DB:
    def __init__(self):
        self.services = [
            Service("Hydraulik", "5"),
            Service("Ginekolog", "12"),
            Service("Holowanie", "0"),
        ]

    def get_services(self, id_):
        return self.services
                
