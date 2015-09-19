import json

from json import JSONEncoder


class BankEncoder(JSONEncoder):
    def default(self, obj):
        if isinstance(obj, JSONSerializable):
            return obj.to_json()

class JSONSerializable:
    pass

def dumps(*args, **kwargs):
    return json.dumps(*args, cls=BankEncoder, **kwargs)

def loads(*args, **kwargs):
    return json.loads(*args, **kwargs)
