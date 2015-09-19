from bankjson import JSONSerializable


class Service(JSONSerializable):
    def __init__(self, name, tokens):
        self.name = name
        self.tokens = tokens

    def to_json(self):
        return {'name': self.name,
            'tokens': self.tokens,
            }
