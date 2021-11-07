import torch
import torchvision
from torchvision import transforms as T
import cv2

def load_model():
    # model = torchvision.models.detection.keypointrcnn_resnet50_fpn(pretrained=True)
    model = torch.load('models/keypointrcnn_resnet50_fpn.pth')
    model.eval()
    return model

# torch.save(model, 'models/keypointrcnn_resnet50_fpn.pth')
def extract_pose_crop(img, model):
    keypoints_name = ['nose','left_eye','right_eye','left_ear','right_ear','left_shoulder','right_shoulder','left_elbow','right_elbow','left_wrist','right_wrist','left_hip','right_hip','left_knee', 'right_knee', 'left_ankle','right_ankle']

    transforms = T.Compose([T.ToTensor()])
    img_tensor = transforms(img)

    output = model([img_tensor])[0]

    keypoints = output['keypoints'].detach().numpy()[0]
    keypoints_scores = output['keypoints_scores'].detach().numpy()[0]

    # for idx in range(keypoints.shape[0]):
    #     if keypoints_scores[idx] > 2:
    #         c = (int(keypoints[idx][0]),int(keypoints[idx][1]))
    #         print(c)
    #         cv2.circle(img, center = c, radius = 1, color = (255,255,255),thickness = -1)
    #         cv2.putText(img, keypoints_name[idx], c, cv2.FONT_HERSHEY_SIMPLEX, 1, (0,0,255))

    # cv2.imshow('image', img)
    # cv2.waitKey(0)
    # cv2.destroyAllWindows()
    # print(keypoints_scores)
    x_extremes = [img.shape[1], 0]
    y_extremes = [img.shape[0], 0]

    idx_of_interest = [5, 6, 11, 12]

    for i in idx_of_interest:
        if keypoints_scores[i] > 0:
            x_extremes[0] = min(x_extremes[0], keypoints[i][0])
            y_extremes[0] = min(y_extremes[0], keypoints[i][1])
            x_extremes[1] = max(x_extremes[1], keypoints[i][0])
            y_extremes[1] = max(y_extremes[1], keypoints[i][1])

    x_extremes = [int(x) for x in x_extremes]
    y_extremes = [int(y) for y in y_extremes]
    final_img = img[y_extremes[0]:y_extremes[1], x_extremes[0]:x_extremes[1], :]
    
    if final_img.shape[0] * final_img.shape[1] < 1000:
        final_img = img
    return final_img

def extract_cloth_img(img):
    model = load_model()
    output_img = extract_pose_crop(img, model)
    return output_img

if __name__ == '__main__':
    
    IMAGE_PATH = 'inventory/10651.jpg'

    img = cv2.imread(IMAGE_PATH)
    # print(img.shape)
    final_img = extract_cloth_img(img)
    cv2.imshow('Image',final_img)
    cv2.waitKey(0)
    cv2.destroyAllWindows()

    # cv2.imwrite(IMAGE_PATH[:-4] + 'cropped' + IMAGE_PATH[-4:], final_img)