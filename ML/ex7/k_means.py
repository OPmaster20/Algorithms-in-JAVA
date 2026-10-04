from sklearn.cluster import KMeans
import numpy as np
from scipy.io import loadmat
import matplotlib.pyplot as plt
from PIL import Image

# loading example data
data = loadmat("ex7data2.mat")
X = data["X"]
# loading image
img = Image.open("bird_small.png")
img_array = np.array(img)
pixels = img_array.reshape(-1, 3)

# Building K-means model
kmeans = KMeans(
    n_clusters=8,      # number of k
    random_state=42,
    n_init=100
)

def draw():
    plt.scatter(
        X[:, 0],
        X[:, 1],
        c=kmeans.labels_,
        cmap="viridis"
    )
    plt.scatter(
        kmeans.cluster_centers_[:, 0],
        kmeans.cluster_centers_[:, 1],
        s=200,
        c="red",
        marker="X"
    )
    plt.show()

# Training
kmeans.fit(pixels)

'''
print("Labels:")
print(kmeans.labels_)

print("Centers:")
print(kmeans.cluster_centers_)

draw()
'''
labels = kmeans.labels_
colors = kmeans.cluster_centers_.astype(np.uint8)
new_pixels = colors[labels]
new_img = new_pixels.reshape(img_array.shape)
Image.fromarray(new_img).save("compressed.jpg")

