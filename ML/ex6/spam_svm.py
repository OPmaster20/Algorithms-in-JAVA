import numpy as np
from sklearn.svm import SVC
from scipy.io import loadmat
from sklearn.metrics import accuracy_score

# loading data
train_data = loadmat("spamTrain.mat")
test_data = loadmat("spamTest.mat")

X = train_data["X"]
y = train_data["y"].ravel()

X_test = test_data["Xtest"]
y_test = test_data["ytest"].ravel()

# C
C = 0.1

clf = SVC(kernel="linear",  C=C)
clf.fit(X, y)

# train accuracy
y_train_pred = clf.predict(X)
train_acc = accuracy_score(y, y_train_pred)
print(f"Training Accuracy: {train_acc:.4f}")

# test accuracy
y_pred = clf.predict(X_test)
acc = accuracy_score(y_test, y_pred)
print(f"Test Accuracy: {acc:.4f}")