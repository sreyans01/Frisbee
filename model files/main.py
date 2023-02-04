from flask import Flask, jsonify, request
from flask_ngrok import run_with_ngrok
import requests
import urllib
import shutil
from predict import get_similar_list, get_similar_list_for_list, get_list_final, download_video
app = Flask(__name__)
run_with_ngrok(app) 
@app.route('/')
def welcome():
    return jsonify({"Name":'Welcome to Frisbee API! Use /predict to get predictions'})

@app.route('/predict/', methods=['GET', 'POST'])
def predict():
    json_data = request.json
    url = json_data['url']
    urllib.request.urlretrieve(url,'test.jpg')
    res_list, valid = get_similar_list('test.jpg')
    if valid:
        is_valid = "valid"
    else:
        is_valid = "The recommendation provided may not be accurate."
    return jsonify({"recommendation": res_list, "message": is_valid})

@app.route('/predictlist/', methods=['GET','POST'])
def predictlist():
    json_data = request.json
    url_list = json_data['url_list']
    res_coll = []
    pred_coll = []
    valid = False
    for url in url_list:
        urllib.request.urlretrieve(url,'test.jpg')
        res_list, pred_list, valid_temp = get_similar_list_for_list('test.jpg')
        res_coll.append(res_list)
        pred_coll.append(pred_list)
        valid = valid or valid_temp
    get_final = get_list_final(res_coll, pred_coll)
    if valid:
        is_valid = "valid"
    else:
        is_valid = "The recommendation provided may not be accurate."
    return jsonify({'recommendation': get_final, "message": is_valid})

@app.route('/predictyoutube/', methods=['GET','POST'])
def predictyoutube():
  json_data = request.json
  url = json_data['url']
  try:
    ret_d = download_video(url)
    if ret_d:
      print('Hoorah!')

    res_coll = []
    pred_coll = []

    res_coll_invalid = []
    pred_coll_invalid = []
    valid = False
    get_final = []
    print('Debug')
    print('Debug!!')

    for image_name in ['2.jpg','6.jpg','10.jpg']:
        print('Debug!)')
        res_list, pred_list, valid_temp = get_similar_list_for_list('frames/'+image_name)
        if valid_temp:
          res_coll.append(res_list)
          pred_coll.append(pred_list)
        else:
          res_coll_invalid.append(res_list)
          pred_coll_invalid.append(pred_list)
        print(image_name, valid)
        valid = valid or valid_temp
  
    if len(res_coll) == 0:
      get_final = get_list_final(res_coll_invalid, pred_coll_invalid)
    else:  
      get_final = get_list_final(res_coll, pred_coll)
    if valid:
        is_valid = "valid"
    else:
        is_valid = "The recommendation provided may not be accurate."
    return jsonify({'recommendation': get_final, "message": is_valid})
  except Exception as e:
    res_list, valid = get_similar_list('test.jpg')
    if valid:
      is_valid = "valid"
    else:
      is_valid = "The recommendation provided may not be accurate."
    return jsonify({"recommendation": res_list, "message": "Recommendation may not be accurate!"})

if __name__ == '__main__':
    app.run()
