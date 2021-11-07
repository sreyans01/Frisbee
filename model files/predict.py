import cv2
from segper import extract_segmented_img
from segcloth import extract_cloth_img
from feature_vector_extraction import get_feature_vectors, cosine_similarity
import os
import json

IMAGE_PATH = 'inventory/10651.jpg'
INVENTORY_PATH = 'inventory'
def image_resize(image, width = None, height = None, inter = cv2.INTER_AREA):
    dim = None
    (h, w) = image.shape[:2]
    if width is None and height is None:
        return image
    if width is None:
        r = height / float(h)
        dim = (int(w * r), height)

    else:
        r = width / float(w)
        dim = (width, int(h * r))

    resized = cv2.resize(image, dim, interpolation = inter)
    return resized

def get_embedding(img_path):
    orig_img_unresized = cv2.imread(img_path)
    orig_img = image_resize(orig_img_unresized, height = 600)
    person_img = extract_segmented_img(orig_img)
    clothing_img = extract_cloth_img(person_img)
    feature_vector_img = get_feature_vectors(clothing_img)
    return feature_vector_img

def create_inventory_json(existing_json = False):
    data = {}
    if existing_json:
        with open('inventory_embeddings.json', 'r') as f:
            data = json.load(f)

    list_of_inventory = os.listdir(INVENTORY_PATH)

    for filename in list_of_inventory:
        item_id = os.path.splitext(filename)[0]
        if item_id not in data:
            print('Evaluating item_id: {}'.format(item_id))
            embedding_item = get_embedding(os.path.join(INVENTORY_PATH, filename))
            data[item_id] = embedding_item.tolist()
    
    with open('inventory_embeddings.json', 'w') as f:
        json.dump(data, f)
    
    return data

def get_similar_list(img_path):
    vector_embedding = get_embedding(img_path)
    data = {}
    with open('inventory_embeddings.json', 'r') as f:
        data = json.load(f)

    similarity_list = []
    for ele in data.keys():
        similarity_list.append((ele,cosine_similarity(data[ele], vector_embedding)))

    similarity_list.sort(key=lambda x: x[-1], reverse=True)

    res = [x[0] for x in similarity_list]

    return res

if __name__ == '__main__':
    list_workdir = os.listdir()
    flag = False
    if 'inventory_embeddings.json' in list_workdir:
        flag = True 
    data = create_inventory_json(flag)
    
    recommend_list = get_similar_list(IMAGE_PATH)
    print(recommend_list)