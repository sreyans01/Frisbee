import cv2
import numpy as np
import time

def get_output_layers(net):
    layer_names = net.getLayerNames()
    output_layers = [layer_names[i[0] - 1] for i in net.getUnconnectedOutLayers()]
    return output_layers

def crop(img, x, y, x1, y1):
    x_min = min(x, x1)
    y_min = min(y, y1)
    x_max = max(x, x1)
    y_max = max(y, y1)
    ret = img[y_min:y_max,x_min:x_max,:]
    return ret
TXT_PATH = 'conf_files/yolov3.txt'
CFG_PATH = 'conf_files/yolov3.cfg'
WT_PATH = 'conf_files/yolov3.weights'

def get_person_of_interest(image):
    try:
        Width = image.shape[1]
        Height = image.shape[0]
        scale = 0.00392

        classes = None
        with open(TXT_PATH, 'r') as f:
            classes = [line.strip() for line in f.readlines()]
        COLORS = np.random.uniform(0, 255, size=(len(classes), 3))
        net = cv2.dnn.readNet(WT_PATH, CFG_PATH)
        blob = cv2.dnn.blobFromImage(image, scale, (416,416), (0,0,0), True, crop=False)
        net.setInput(blob)


        outs = net.forward(get_output_layers(net))

        class_ids = []
        confidences = []
        boxes = []
        conf_threshold = 0.5
        nms_threshold = 0.4
        final_index = []
        for out in outs:
            for detection in out:
                scores = detection[5:]
                class_id = np.argmax(scores)
                confidence = scores[class_id]
                if confidence > 0.5:
                    center_x = int(detection[0] * Width)
                    center_y = int(detection[1] * Height)
                    w = int(detection[2] * Width)
                    h = int(detection[3] * Height)
                    x = center_x - w / 2
                    y = center_y - h / 2
                    class_ids.append(class_id)
                    confidences.append(float(confidence))
                    boxes.append([x, y, w, h])

        indices = cv2.dnn.NMSBoxes(boxes, confidences, conf_threshold, nms_threshold)

        for i in indices:
            i = i[0]
            if str(classes[class_ids[i]]) == 'person':
                final_index.append((confidences[i], i))

        final_index.sort()
        i = final_index[0][1]
        box = boxes[i]
        x = box[0]
        y = box[1]
        w = box[2]
        h = box[3]

        final_image = crop(image, round(x), round(y), round(x+w), round(y+h))
        return final_image, True
    except Exception as e:
        return image, False
if __name__ == '__main__':

    IMAGE_PATH = 'images/trial1.jpeg'
    tic = time.time()
    image = cv2.imread(IMAGE_PATH)
    final_image = get_person_of_interest(image)
    cv2.imwrite("object-detection.jpg", final_image)
    toc = time.time()
    print(toc-tic)
