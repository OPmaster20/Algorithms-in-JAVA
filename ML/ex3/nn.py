from scipy.io import loadmat
import torch
import torch.nn as nn

# check if GPU is available
print(torch.cuda.is_available())
print(torch.cuda.device_count())
print(torch.cuda.get_device_name(0))
# reading data
data = loadmat("ex3data1.mat")
weights = loadmat("ex3weights.mat")

# get weights
theta1 = torch.tensor(weights['Theta1'], dtype=torch.float32)
theta2 = torch.tensor(weights['Theta2'], dtype=torch.float32)
# use cuda
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
# get training data
X = data['X']
y = data['y']
# transfer to the tensor
X = torch.tensor(X, dtype=torch.float32)
y = torch.tensor(y.flatten(), dtype=torch.long)
y = torch.tensor(y.flatten(), dtype=torch.long)

y[y == 10] = 0
y[y != 0] -= 1

X = X.to(device)
y = y.to(device)
theta1 = theta1.to(device)
theta2 = theta2.to(device)

# training times
epochs = 1000
# 2 layer full connected network
class Net(nn.Module):
    def __init__(self):
        super().__init__()
        self.fc1 = nn.Linear(400,25)
        self.fc2 = nn.Linear(25,10)
        self.sigmoid = nn.Sigmoid()
        # replace the default weight
        with torch.no_grad():
            self.fc1.weight.copy_(theta1[:,1:])
            self.fc1.bias.copy_(theta1[:,0])
            self.fc2.weight.copy_(theta2[:,1:])
            self.fc2.bias.copy_(theta2[:,0])
    def forward(self,x):
        x = self.sigmoid(self.fc1(x))
        x = self.fc2(x)
        return x

model = Net().to(device)
# cost function
criterion = nn.CrossEntropyLoss()
# optimizer
optimizer = torch.optim.Adam(
    model.parameters(),
    lr=0.001,
    weight_decay=1e-4
)

# training
for epoch in range(epochs):
    # Forward Propagation
    outputs = model(X)
    loss = criterion(outputs, y)
    # set gradient 0
    optimizer.zero_grad()
    # Backward Propagation
    loss.backward()
    # update parameters
    optimizer.step()

    if epoch % 10 == 0:
        print(f"Epoch {epoch}, Loss={loss.item():.4f}")

# prediction
with torch.no_grad():
    outputs = model(X)
    pred = torch.argmax(outputs, dim=1)
    accuracy = (pred == y).float().mean()
    print("Accuracy:", accuracy.item())
