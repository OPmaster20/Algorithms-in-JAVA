import numpy as np
import matplotlib.pyplot as plt
# Linear regression with single variable input
# reading data
data = np.loadtxt("ex1data1.txt", delimiter=",")
# training sample
x = data[:, 0]
y = data[:, 1]
# number of training samples
m = len(y)

X = np.column_stack((np.ones(m), x))   # shape (m, 2)

theta = np.zeros(2)   # shape (2,)
# learning rate
alpha = 0.005
# loop times
iterations = 1000

# hypothesis hθ(x) is given by the linear model
# which is theta[0] + theta[1] * X
def hypothesis(X, theta):
    return X.dot(theta)

# The objective of linear regression is to minimize the cost function
# which is (1/(2*m)) * sum of [hθ(x) - y ** 2]
def costFunction(X, y, theta):
    h = hypothesis(X, theta)
    return (1/(2*m)) * np.sum((h - y)**2)

def gradientDescent(X, y, theta, alpha, iterations):
    # Loop
    for i in range(iterations):
        h = hypothesis(X, theta)
        error = h - y
        gradient = (1/m) * X.T.dot(error)
        # update theta
        theta = theta - alpha * gradient
    return theta

print("Cost before =", costFunction(X, y, theta))
theta = gradientDescent(X, y, theta, alpha, iterations)

print("theta0 =", theta[0])
print("theta1 =", theta[1])
print("Cost after gradient Descent =", costFunction(X, y, theta))
# prediction
predict1 = [1, 3.5] * theta
predict2 = [1, 7] * theta
print("predict1 =", predict1[1])
print("predict2 =", predict2[1])
# plot
plt.plot(x, y, 'rx', markersize=10)
plt.plot(x, hypothesis(X, theta), 'b-')
plt.ylabel('Profit in $10,000s')
plt.xlabel('Population of City in 10,000s')
plt.show()
