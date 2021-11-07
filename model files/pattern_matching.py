import cv2
import matplotlib.pyplot as plt

img1 = cv2.imread('pattern_images/9.png')
img2 = cv2.imread('pattern_images/10.png')

img1 = cv2.cvtColor(img1, cv2.COLOR_BGR2GRAY)
img2 = cv2.cvtColor(img2, cv2.COLOR_BGR2GRAY)
sift = cv2.SIFT_create()

keypoints1, descriptors1 = sift.detectAndCompute(img1, None)
keypoints2, descriptors2 = sift.detectAndCompute(img2, None)

print(len(keypoints2))
print(descriptors1.shape)
print(descriptors2[1])
print(descriptors2.max())

bf = cv2.BFMatcher(cv2.NORM_L1, crossCheck=True)
matches = bf.match(descriptors1, descriptors2)
matches = sorted(matches, key = lambda x:x.distance)
print(matches[0].distance)
print(len(matches))

# print(keypoints2)
img3 = cv2.drawMatches(img1, keypoints1, img2, keypoints2, matches[:30], img2, flags=2)
# cv2.imshow("Image", img3)
# cv2.waitKey()
# cv2.destroyAllWindows()

match1 = [x.distance for x in matches]

img1 = cv2.imread('pattern_images/9.png')
img2 = cv2.imread('pattern_images/10.png')

img1 = cv2.cvtColor(img1, cv2.COLOR_BGR2GRAY)
img2 = cv2.cvtColor(img2, cv2.COLOR_BGR2GRAY)
sift = cv2.SIFT_create()

keypoints1, descriptors1 = sift.detectAndCompute(img1, None)
keypoints2, descriptors2 = sift.detectAndCompute(img2, None)

bf = cv2.BFMatcher(cv2.NORM_L2, crossCheck=True)
matches = bf.match(descriptors1, descriptors2)
matches = sorted(matches, key = lambda x:x.distance)

match2 = [x.distance for x in matches]