import numpy as np
import matplotlib.pyplot as plt
# Linear regression with mult data
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

# parameters
alpha = 0.01
iterations = 2000

# Regularization lambda
lambda_ = 1.0

theta = np.zeros(X.shape[1])

def hypothesis(X, theta):
    return X.dot(theta)

def costFunction(X, y, theta):
    h = X @ theta
    return (np.sum((h - y) ** 2) / (2 * m) + lambda_ / (2 * m) * np.sum(theta[1:] ** 2))

def gradientDescent(X, y, theta, alpha, iterations):
    J_history = []

    for _ in range(iterations):
        h = X @ theta
        error = h - y
        gradient = (1 / m) * (X.T @ error)
        reg = (lambda_ / m) * theta
        reg[0] = 0
        gradient += reg
        theta -= alpha * gradient
        J_history.append(
            costFunction(X, y, theta)
        )

    return theta, J_history

theta, J_history = gradientDescent(
    X, y, theta, alpha, iterations
)

# prediction
def predict(size, bedrooms):
    x = np.array([size, bedrooms])
    x = (x - mu) / sigma
    x = np.insert(x, 0, 1)
    return x.dot(theta)

print(predict(1000, 2))

plt.plot(J_history)
plt.xlabel("Iterations")
plt.ylabel("Cost J")
plt.title("Gradient Descent Convergence")
plt.show()