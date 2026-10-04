import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.svm import SVC
from scipy.io import loadmat
from sklearn.metrics import accuracy_score
import matplotlib.pyplot as plt

# loading data
data = loadmat("ex6data3.mat")

print(data.keys())
X = data["X"]
y = data["y"].ravel()

X_val = data["Xval"]
y_val = data["yval"].ravel()

'''
X_train, X_test, y_train, y_test = train_test_split(
    X, y,
    test_size=0.2,
    random_state=42
)
'''

# search for best values
# define test values
C_values = [0.01, 0.03, 0.1, 0.3, 1, 3, 10, 30]
sigma_values = [0.01, 0.03, 0.1, 0.3, 1, 3, 10, 30]

output_value = 0
best_C = 0
best_sigma = 0

# Kernel function
def gaussian_kernel(X, Y):
    X_norm = np.sum(X ** 2, axis=1).reshape(-1, 1)
    Y_norm = np.sum(Y ** 2, axis=1).reshape(1, -1)
    dist2 = X_norm + Y_norm - 2 * X @ Y.T
    return np.exp(-dist2 / (2 * sigma ** 2))

Time = 0
sigma = 0
# loop
for C in C_values:
    for s in sigma_values:
        Time += 1
        sigma = s
        # building svm classifier
        clf = SVC(
            kernel=gaussian_kernel,  # 线性、rbf、多项式等
            C=C
        )
        # training
        clf.fit(X, y)
        val_score = clf.score(X_val, y_val)
        print(f"Test round {Time} - VA: {val_score}")

        if val_score >= output_value:
            output_value = val_score
            best_C = C
            best_sigma = s

# try best value
sigma = best_sigma
clf = SVC(
        kernel=gaussian_kernel,  # 线性、rbf、多项式等
        C=best_C
)
clf.fit(X, y)
val_score = clf.score(X_val, y_val)
y_pred = clf.predict(X)
acc = accuracy_score(y, y_pred)


# draw and plot
def draw_linear_fit():
    plt.figure(figsize=(8, 6))
    plt.scatter(
        X[:, 0],
        X[:, 1],
        c=y,
        cmap='bwr',
        edgecolors='k'
    )
    ax = plt.gca()
    xlim = ax.get_xlim()
    ylim = ax.get_ylim()
    xx = np.linspace(
        xlim[0],
        xlim[1],
        200
    )
    yy = np.linspace(
        ylim[0],
        ylim[1],
        200
    )
    YY, XX = np.meshgrid(yy, xx)
    xy = np.c_[XX.ravel(), YY.ravel()]
    Z = clf.decision_function(xy)
    Z = Z.reshape(XX.shape)
    ax.contour(
        XX,
        YY,
        Z,
        levels=[-1, 0, 1],
        alpha=0.8,
        linestyles=[
            '--',
            '-',
            '--'
        ]
    )
    plt.title("SVM Decision Boundary")
    plt.show()

print(f"Best VA: {val_score}")
print(f"Accuracy: {acc:.4f}")
draw_linear_fit()