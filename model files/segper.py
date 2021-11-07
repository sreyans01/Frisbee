from typing_extensions import final
import torch
import torchvision
import cv2
import numpy as np

def load_model():
    device = "cuda" if torch.cuda.is_available() else "cpu"
    model = torch.load('models/deepnet_resnet_pretrained.pth')
    model.to(device).eval()
    return model

def segment_image(img, model):
    device = "cuda" if torch.cuda.is_available() else "cpu"

    imagenet_stats = [[0.485, 0.456, 0.406], [0.229, 0.224, 0.225]]
    preprocess = torchvision.transforms.Compose([torchvision.transforms.ToTensor(),
                                               torchvision.transforms.Normalize(mean = imagenet_stats[0],
                                                                                std  = imagenet_stats[1])])
    input_tensor = preprocess(img).unsqueeze(0)
    # print("Tensor shape: ",input_tensor.shape)
    input_tensor = input_tensor.to(device)

    with torch.no_grad():
        output = model(input_tensor)
        output = output["out"][0]
        output = output.argmax(0)

    return output.cpu().numpy()

def extract_segmented_img(img):
    model = load_model()
    output = segment_image(img, model)
    mask = (output == 15)*255
    mask = mask.astype(np.uint8)
    contours, hierarchy = cv2.findContours(mask, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
    
    max_area = -1
    max_idx = -1

    for idx in range(len(contours)):
        ct_area = cv2.contourArea(contours[idx])
        if ct_area > max_area:
            max_area = ct_area
            max_idx = idx
    x, y, w, h = cv2.boundingRect(contours[max_idx])

    final_img = img[y:y+h, x:x+w, :]

    if final_img.shape[0] * final_img.shape[1] < 1000:
        final_img = img
    return final_img

if __name__ == '__main__':
    # model = load_model()
    img_demo = cv2.imread('inventory/10651.jpg')
    print("Image shape before passing to the function : ", img_demo.shape)
    model = load_model()
    print("Model Loading complete!")
    res = segment_image(img_demo, model)
    print("Result shape: ",res.shape)
    print(res.max())
    mask = (res == 15)*255
    mask = mask.astype(np.uint8)
    contours, hierarchy = cv2.findContours(mask, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

    max_area = -1
    max_idx = -1

    for idx in range(len(contours)):
        ct_area = cv2.contourArea(contours[idx])
        if ct_area > max_area:
            max_area = ct_area
            max_idx = idx
    x, y, w, h = cv2.boundingRect(contours[max_idx])

    final_img = img_demo[y:y+h, x:x+w, :]

    # torch.save(model, "models/deepnet_resnet_pretrained.pth")
    cv2.imshow("Image Segmentation Output", final_img)
    cv2.waitKey(0)
    cv2.destroyAllWindows()
