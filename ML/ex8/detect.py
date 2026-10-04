import numpy as np
from scipy.io import loadmat
from scipy.stats import multivariate_normal
import matplotlib.pyplot as plt

# Loading data
data = loadmat("ex8data1.mat")
X = np.array(data["X"])            # 训练集 (n, 2)

mu = np.mean(X, axis=0)
cov = np.cov(X.T)
rv = multivariate_normal(mean=mu, cov=cov)

X_val = np.array(data["Xval"])     # (m, 2)
y_val = np.array(data["yval"]).ravel()   # (m,)  0=正常, 1=异常

p_val = rv.pdf(X_val)              # (m,)

def select_threshold(y_val, p_val):
    best_epsilon = 0
    best_F1 = 0
    F1 = 0

    step_size = (p_val.max() - p_val.min()) / 1000
    epsilons = np.arange(p_val.min(), p_val.max(), step_size)

    for epsilon in epsilons:
        y_pred = (p_val < epsilon).astype(int)   # 小于阈值 → 异常

        # TP / FP / FN
        tp = np.sum((y_pred == 1) & (y_val == 1))
        fp = np.sum((y_pred == 1) & (y_val == 0))
        fn = np.sum((y_pred == 0) & (y_val == 1))

        # PR
        prec = tp / (tp + fp) if (tp + fp) > 0 else 0
        rec  = tp / (tp + fn) if (tp + fn) > 0 else 0

        # F1
        F1 = 2 * prec * rec / (prec + rec) if (prec + rec) > 0 else 0

        if F1 > best_F1:
            best_F1 = F1
            best_epsilon = epsilon

    return best_epsilon, best_F1

def draw():
    plt.figure(figsize=(7, 6))
    plt.scatter(X[:, 0], X[:, 1], marker='x')
    x1 = np.linspace(0, 30, 100)
    x2 = np.linspace(0, 30, 100)
    X1, X2 = np.meshgrid(x1, x2)
    pos = np.dstack((X1, X2))
    rv = multivariate_normal(mu, cov)
    Z = rv.pdf(pos)
    plt.contour(
        X1,
        X2,
        Z,
        levels=10
    )
    plt.xlabel("Latency")
    plt.ylabel("Throughput")
    plt.title("Gaussian Fit")
    plt.show()

epsilon, F1 = select_threshold(y_val, p_val)
print(f"Best epsilon = {epsilon:.6e}")
print(f"Best F1       = {F1:.4f}")

outliers = X_val[p_val < epsilon]
print("Outliers found:", outliers.shape[0])

draw()