from scipy.io import loadmat
import numpy as np

# reading data
data = loadmat("ex4data1.mat")
weights = loadmat("ex4weights.mat")

X = data['X']
y = data['y'].flatten()

# unit
a1 = None
z2 = None
a2 = None

z3 = None
a3 = None

# get weights
#Theta1 = weights['Theta1']
#Theta2 = weights['Theta2']

eps = 0.12
Theta1 = np.random.rand(25,401) * 2 * eps - eps
Theta2 = np.random.rand(10,26) * 2 * eps - eps

# number of feature
m = len(y)

y[y == 10] = 0
Y = np.eye(10)[y]

# learning rate
alpha = 0.99
# iterations times
iterations = 2500

# lambda 
lambda_ = 1.0

# sigmoid function
def sigmoid(z):
    z = np.clip(z, -500, 500)
    return 1 / (1 + np.exp(-z))

def sigmoid_gradient(z):
    s = sigmoid(z)
    return s * (1 - s)

# Forward propagation
def forward(Theta1, Theta2):
    global a1, z2, a2, z3, a3
    a1 = np.column_stack((np.ones(m), X))
    
    z2 = a1 @ Theta1.T
    a2 = sigmoid(z2)
    a2 = np.column_stack((np.ones(m), a2))
    
    z3 = a2 @ Theta2.T
    a3 = sigmoid(z3)
    return a3

# cost function
def nn_cost(Y, H, Theta1, Theta2):
    m = len(Y)
    eps = 1e-10
    
    cost = (-1 / m) * np.sum(
    Y * np.log(H + eps)
    + (1 - Y) * np.log(1 - H + eps)
    )
    
    reg = (lambda_ / (2 * m)) * (
    np.sum(Theta1[:, 1:] ** 2)
    + np.sum(Theta2[:, 1:] ** 2)
    )
    
    return cost + reg

# backforward propagation
def backforward(Theta1, Theta2):
    
    delta3 = a3 - Y
    
    delta2 = (
    delta3 @ Theta2[:,1:]
    ) * sigmoid_gradient(z2)
    
    Delta1 = delta2.T @ a1
    Delta2 = delta3.T @ a2
    
    Theta1_grad = Delta1 / m
    Theta2_grad = Delta2 / m
    
    Theta1_grad[:,1:] += (lambda_/m) * Theta1[:,1:]
    Theta2_grad[:,1:] += (lambda_/m) * Theta2[:,1:]
    
    return Theta1_grad, Theta2_grad


def gradient_descent(X, y, Theta1,Theta2, alpha, iterations):
    cost_history = []
    for _ in range(iterations):
    
        a3 = forward(Theta1, Theta2)
        cost = nn_cost(Y, a3, Theta1, Theta2)
        # update theta
        Theta1_grad, Theta2_grad = backforward(Theta1, Theta2)
        
        for t in range(5):

            i = np.random.randint(Theta1.shape[0])
            j = np.random.randint(Theta1.shape[1])
        
            theta_plus = Theta1.copy()
            theta_minus = Theta1.copy()
        
            theta_plus[i,j] += eps
            theta_minus[i,j] -= eps
        
            J_plus = nn_cost(
                Y,
                forward(theta_plus, Theta2),
                theta_plus,
                Theta2
            )
        
            J_minus = nn_cost(
                Y,
                forward(theta_minus, Theta2),
                theta_minus,
                Theta2
            )
        
            num_grad = (J_plus - J_minus)/(2*eps)
        
            print(
                f"Theta1[{i},{j}]",
                "Numerical=", num_grad,
                "Backprop=", Theta1_grad[i,j]
            )
        
        Theta1 = Theta1 - alpha * Theta1_grad
        Theta2 = Theta2 - alpha * Theta2_grad

        
        print(f"Iterations {_ + 1} Cost - ", cost)
        cost_history.append(cost)

    return Theta1, Theta2, cost_history

theta1, theta2, costs = gradient_descent(X,y,Theta1,Theta2, alpha, iterations)

pred = np.argmax(a3, axis=1)
acc = np.mean(pred == y)

print("accuracy =", acc)