import numpy as np
import matplotlib.pyplot as plt

data = np.loadtxt("ex2data1.txt", delimiter=",")

X = data[:, :2]
y = data[:, 2]

# feature scaling
mu = np.mean(X, axis=0)
sigma = np.std(X, axis=0)
X_norm = (X - mu) / sigma

m = len(y)

X = np.column_stack((np.ones(m), X_norm))
theta = np.zeros(X.shape[1])

# learning rate
alpha = 0.001
# iterations times
iterations = 1500

# mark the dataset
pos = y == 1
neg = y == 0

# convex function (help u to find local optima)
def sigmoid(z):
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

print("Theta:")
print(theta)
print("Final Cost:")
print(costs[-1])

def predict(X, theta):
    probs = hypothesis(X, theta)
    return (probs >= 0.5).astype(int)

pred = predict(X, theta)
accuracy = np.mean(pred == y) * 100
print(f"Accuracy = {accuracy:.2f}%")

exam1 = X[:, 1]
exam2 = X[:, 2]

plt.figure(figsize=(8,6))
# y=1
plt.scatter(exam1[pos],exam2[pos],c='b',marker='+',label='Admitted')
# y=0
plt.scatter(exam1[neg],exam2[neg],c='r',marker='o',label='Not Admitted')

plt.xlabel('Exam 1 Score')
plt.ylabel('Exam 2 Score')
plt.legend()
plt.grid(True)

x_boundary = np.array([exam1.min(),exam1.max()])
y_boundary = -(theta[0] + theta[1] * x_boundary) / theta[2]

plt.plot(x_boundary,y_boundary,'g-',linewidth=2,label='Decision Boundary')
plt.show()