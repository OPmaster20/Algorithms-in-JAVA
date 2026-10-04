from scipy.io import loadmat
import numpy as np
import matplotlib.pyplot as plt

# reading data
data = loadmat("ex5data1.mat")

# training set
data_train_X = data["X"]
data_train_y = data["y"].ravel()

# validation set
data_val_X = data["Xval"]
data_val_y = data["yval"].ravel()

# test set
data_test_X = data["Xtest"]
data_test_y = data["ytest"].ravel()

X = np.column_stack((np.ones(len(data_train_y)), data_train_X))   # shape (m, 2)

theta = np.zeros(2)   # shape (2,)
# learning rate
alpha = 0.001
# loop times
iterations = 1000

lambda_ = 1.0

# hypothesis function
def hypothesis(X, theta):
    return X.dot(theta)

# cost function
def cost_function(X, y, theta, lambda_):
    m = len(y)
    h = hypothesis(X, theta)
    cost = (1/(2*m)) * np.sum((h - y)**2) + (lambda_ / (2 * m)) * np.sum(theta[1:] ** 2)
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

        cost = cost_function(X, y, theta, lambda_)

        print(f"Iterations {_ + 1} Cost - ", cost)
        cost_history.append(cost)

    return theta, cost_history



theta, costs = gradient_descent(X,data_train_y,theta,alpha,iterations)

def draw_linear_fit():
    plt.scatter(
        data_train_X,
        data_train_y,
        color="blue",
        label="Training Data"
    )
    y_pred = hypothesis(X, theta)
    plt.plot(
        data_train_X,
        y_pred,
        color="red",
        label="Linear Regression"
    )
    plt.legend()
    plt.show()

def draw_learning_curves():
    m = len(data_train_y)
    error_train = []
    error_val = []
    X_val = np.column_stack(
        (np.ones(len(data_val_y)), data_val_X)
    )
    for i in range(2, m + 1):

        X_sub = X[:i]
        y_sub = data_train_y[:i]

        theta_init = np.zeros(2)

        theta_sub, _ = gradient_descent(
            X_sub,
            y_sub,
            theta_init,
            alpha,
            iterations,
        )

        train_error = cost_function(
            X_sub,
            y_sub,
            theta_sub,
            0
        )

        val_error = cost_function(
            X_val,
            data_val_y,
            theta_sub,
            0
        )

        error_train.append(train_error)
        error_val.append(val_error)

    plt.plot(
        range(2, m+1),
        error_train,
        label="Train"
    )

    plt.plot(
        range(2, m+1),
        error_val,
        label="Validation"
    )

    plt.xlabel("Training Examples")
    plt.ylabel("Error")
    plt.legend()
    plt.show()


draw_learning_curves()