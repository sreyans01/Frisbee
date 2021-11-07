from flask import Flask, jsonify, request
import requests
import urllib
from predict import get_similar_list
app = Flask(__name__)

@app.route('/')
def welcome():
    return jsonify({"Name":'Welcome to Frisbee API! Use /predict to get predictions'})

@app.route('/predict/', methods=['GET', 'POST'])
def predict():
    url = request.args.get('url')
    url = url.replace("()","%")
    urllib.request.urlretrieve(url,'test.jpg')
    res_list = get_similar_list('test.jpg')
    return jsonify({"recommendation": res_list})

if __name__ == '__main__':
    app.run(host="192.168.29.81", port = 8001, debug=True)