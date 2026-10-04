import numpy as np
import matplotlib.pyplot as plt

data = np.loadtxt("ex2data2.txt", delimiter=",")


# Feature mapping [extend to 28 feature]
def map_feature(x1, x2):
    out = []
    for i in range(1, 7):
        for j in range(i + 1):
            out.append((x1 ** (i - j)) * (x2 ** j))
    return np.column_stack(out)

X = data[:, :2]
y = data[:, 2]

X_poly = map_feature(X[:, 0],X[:, 1])


# feature scaling
# mu = np.mean(X_poly, axis=0)
# sigma = np.std(X_poly, axis=0)
# X_norm = (X_poly - mu) / sigma

lambda_ = 1.0

m = len(y)

X = np.column_stack((np.ones(m), X_poly))
theta = np.zeros(X.shape[1])

# learning rate
alpha = 0.02
# iterations times
iterations = 2000
# mark the dataset
pos = y == 1
neg = y == 0
exam1 = X[:, 1]
exam2 = X[:, 2]

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

    cost = ((-1 / len(y)) * np.sum(y * np.log(h + eps)+ (1 - y) * np.log(1 - h + eps))) + (lambda_ / (2 * m)) * np.sum(theta[1:] ** 2)
    return cost

def gradient_descent(X, y, theta, alpha, iterations):
    cost_history = []
    m = len(y)
    for _ in range(iterations):
        h = hypothesis(X, theta)
        error = h - y
        gradient = (1 / m) * (X.T @ error)
        reg = (lambda_ / m) * theta
        reg[0] = 0
        gradient += reg
        # update theta
        theta = theta - alpha * gradient

        cost = cost_function(X, y, theta)
        print(f"Iterations {_ + 1} Cost - ", cost)
        cost_history.append(cost)

    return theta, cost_history

theta, costs = gradient_descent(X,y,theta,alpha,iterations)


def predict(X, theta):
    probs = hypothesis(X, theta)
    return (probs >= 0.5).astype(int)

pred = predict(X, theta)
accuracy = np.mean(pred == y) * 100
print(f"Accuracy = {accuracy:.2f}%")

plt.figure(figsize=(10,8))
# y=1
plt.scatter(exam1[pos],exam2[pos],c='b',marker='+',label='Admitted')
# y=0
plt.scatter(exam1[neg],exam2[neg],c='r',marker='o',label='Not Admitted')

plt.xlabel('Exam 1 Score')
plt.ylabel('Exam 2 Score')
plt.legend()
plt.grid(True)

u = np.linspace(data[:,0].min(),data[:,0].max(),50)
v = np.linspace(data[:,1].min(),data[:,1].max(),50)

z = np.zeros((len(u), len(v)))

for i in range(len(u)):
    for j in range(len(v)):
        mapped = map_feature(
        np.array([u[i]]),
        np.array([v[j]])
        )
        mapped = np.column_stack((np.ones(1), mapped))

        z[i,j] = mapped @ theta
plt.contour(u, v, z.T, levels=[0], colors='g')
plt.show()



