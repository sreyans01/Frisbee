import torch
from torchvision import transforms, models
import cv2
from scipy import spatial

IMAGE_PATH = 'images/try.png'

class Img2VecResnet18():
    def __init__(self):
        self.device = torch.device("cpu")
        self.numberFeatures = 512
        self.modelName = "resnet-18"
        self.model, self.featureLayer = self.getFeatureLayer()
        self.model = self.model.to(self.device)
        self.model.eval()
        self.toTensor = transforms.ToTensor()
        self.normalize = transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])
    

    def getFeatureLayer(self):
        # cnnModel = models.resnet18(pretrained=True)
        cnnModel = torch.load('models/resnet18.pth')
        # torch.save(cnnModel, 'models/resnet18.pth')
        layer = cnnModel._modules.get('avgpool')
        self.layer_output_size = 512
        
        return cnnModel, layer

    def getVec(self, img):
        image = self.normalize(self.toTensor(img)).unsqueeze(0).to(self.device)
        embedding = torch.zeros(1, self.numberFeatures, 1, 1)
        def copyData(m, i, o): embedding.copy_(o.data)
        h = self.featureLayer.register_forward_hook(copyData)
        self.model(image)
        h.remove()
        return embedding.numpy()[0, :, 0, 0]


def get_feature_vectors(img):
    try:
        img2vec = Img2VecResnet18()
        img_resized = cv2.resize(img, (224,224), interpolation = cv2.INTER_AREA)
        vec = img2vec.getVec(img_resized)
        valid = True
        return vec, valid
    except Exception as e:
        valid = False
        return [0]*512, valid    

def cosine_similarity(vec1, vec2):
    return 1-spatial.distance.cosine(vec1, vec2)

if __name__ == '__main__':
    image = cv2.imread('inventory/10651.jpg')
    vec = get_feature_vectors(image)
    print(vec)
