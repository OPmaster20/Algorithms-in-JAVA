import numpy as np
from sklearn.decomposition import PCA
from scipy.io import loadmat
import matplotlib.pyplot as plt

data = loadmat("ex7data2.mat")
X = data["X"]


def easy_PCA(X):
    pca = PCA(n_components=2)
    X_pca = pca.fit_transform(X)
    eigenvalues = pca.explained_variance_
    eigenvectors = pca.components_.T
    draw_data(eigenvectors, eigenvalues)

def hard_pca(X):
    X_centered = X - X.mean(axis=0)
    U, S, Vt = np.linalg.svd(X_centered, full_matrices=False)
    X_pca = X_centered @ Vt[:1].T



    X_norm = X - np.mean(X, axis=0)
    Sigma = np.cov(X_norm.T)
    eigenvalues, eigenvectors = np.linalg.eig(Sigma)
    draw_data2(eigenvectors, eigenvalues)
    print("eigenvalues:")
    print(eigenvalues)
    print("eigenvectors:")
    print(eigenvectors)
    print("After PCA:", X_pca.shape)

def draw_data(eigenvectors, eigenvalues):
    # data cental
    mu = np.mean(X, axis=0)

    plt.figure(figsize=(6,6))
    plt.scatter(X[:,0], X[:,1], alpha=0.6)

    # the first feature vector
    plt.arrow(
    mu[0], mu[1],
    eigenvectors[0,0]*eigenvalues[0],
    eigenvectors[1,0]*eigenvalues[0],
    color="red",
    width=0.01,
    label="PC1"
    )

    # second feature vector
    plt.arrow(
    mu[0], mu[1],
    eigenvectors[0,1]*eigenvalues[1],
    eigenvectors[1,1]*eigenvalues[1],
    color="blue",
    width=0.01,
    label="PC2"
    )

    plt.axis("equal")
    plt.legend()
    plt.show()


def draw_data2(X_centered, Vt):
    k = 100
    Z = X_centered @ Vt[:k].T
    X_rec = Z @ Vt[:k]
    X_rec += X.mean(axis=0)
    fig, axes = plt.subplots(1, 2)

    axes[0].imshow(
        X[0].reshape(32, 32),
        cmap='gray'
    )
    axes[0].set_title("Original")

    axes[1].imshow(
        X_rec[0].reshape(32, 32),
        cmap='gray'
    )
    axes[1].set_title("Reconstructed")

    plt.show()


easy_PCA(X)
