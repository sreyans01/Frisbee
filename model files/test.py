import requests

URL = 'http://localhost:5001/predict/'
res = requests.post(URL, json={'url':'https://firebasestorage.googleapis.com/v0/b/frisbee-ed43b.appspot.com/o/uploads%2F1635750168398uploadedImage?alt=media&token=581c037c-c4ae-4c16-921f-0aa188c7b420'})
if res.ok:
    print(res.json())