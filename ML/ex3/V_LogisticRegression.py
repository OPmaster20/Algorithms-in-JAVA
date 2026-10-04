from scipy.io import loadmat
import numpy as np

data = loadmat("ex3data1.mat")
X = data['X']
y = data['y'].flatten()

y[y == 10] = 0
y = (y == 0).astype(int)

m = len(y)

X = np.column_stack((np.ones(m), X))
theta = np.zeros(X.shape[1])

# learning rate
alpha = 0.001
# iterations times
iterations = 1500

# convex function (help u to find local optima)
def sigmoid(z):
    z = np.clip(z, -500, 500)
    return 1 / (1 + np.exp(-z))

# hypothesis function
def hypothesis(X, theta):
    return sigmoid(X @ theta)

# cost function
def cost_function(X, y, theta):
    h = hypothesis(X, theta)
    eps = 1e-10

    cost = (-1 / len(y)) * np.sum(
        y * np.log(h + eps)
        + (1 - y) * np.log(1 - h + eps)
    )

    return cost

def gradient_descent(X, y, theta, alpha, iterations):
    cost_history = []
    m = len(y)
    for _ in range(iterations):
        h = hypothesis(X, theta)
        error = h - y
        gradient = (1 / m) * (X.T @ error)
        # update theta
        theta = theta - alpha * gradient
        cost = cost_function(X, y, theta)
        print(f"Iterations {_ + 1} Cost - ", cost)
        cost_history.append(cost)

    return theta, cost_history

theta, costs = gradient_descent(X,y,theta,alpha,iterations)

pred = (hypothesis(X, theta) >= 0.5).astype(int)
acc = np.mean(pred == y)
print(f"Accuracy = {acc*100:.2f}%")