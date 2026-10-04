import numpy as np
import matplotlib.pyplot as plt

# read data
data = np.loadtxt("ex1data2.txt", delimiter=",")

# features and target
X_data = data[:, 0:2]
y = data[:, 2]

# feature scaling
mu = np.mean(X_data, axis=0)
sigma = np.std(X_data, axis=0)
X_norm = (X_data - mu) / sigma

# add bias term
m = len(y)
X = np.column_stack((np.ones(m), X_norm))

# Regularization lambda
lambda_ = 1.0

# Regularization matrix
def generate_matrix():
    n = X.shape[1]
    A = np.eye(n, dtype=int)
    A[0] = 0
    return A

# normal equation (use pinv for stability) + Regularization
def normalEqn(X, y):
    return np.linalg.pinv(X.T @ X + lambda_ * generate_matrix()) @ X.T @ y

theta = normalEqn(X, y)

# prediction
def predict(size, bedrooms):
    x = np.array([size, bedrooms])
    x = (x - mu) / sigma
    x = np.insert(x, 0, 1)
    return x.dot(theta)

print(predict(1000, 2))
