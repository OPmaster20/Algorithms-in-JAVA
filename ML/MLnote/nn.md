##  Neural network
##### A neural network model is a network composed of numerous logic units organized into different layers, where the output variables of one layer serve as the input variables for the next. The figure below illustrates a three-layer neural network: the first layer is the input layer, the last is the output layer, and the intermediate layer is the hidden layer. We add a bias unit to each layer
##### incorporate activation units into the hidden layers
### Forward propagation algorithm
#### Example：
#### we input X = [x1, x2], Weight W = [w1, w2], Bias variable B
#### The calculation process is as follows: 
### T = x1 * w1 + x2 * w2 + B
### then we input that T into the Sigmoid function: σ(z)=1+e−z1​
### then we get output σ(T)

### Gradient check
##### When applying the gradient descent algorithm to a complex model (such as a neural network), subtle errors may arise; this means that even though the cost appears to be steadily decreasing, the final result might not be the optimal solution.

    To avoid such issues, we employ a technique known as numerical gradient checking. The underlying idea is to verify the correctness of our calculated derivative values ​​by estimating the gradient numerically.

### Random initialization
    Any optimization algorithm requires initial parameters; for neural networks, randomly generated parameters must be used. This is because initializing them to zero would result in duplicate values ​​during the computation of units in the second layer, whereas random parameters offer a wider range of potential optimization paths.

### Training a neural network:

    1. Randomly initialize parameters

    2. Use forward propagation to compute all $h_{\theta}(x)$

    3. Write code to compute the cost function $J$

    4. Use backpropagation to compute all partial derivatives

    5. Use numerical gradient checking to verify these partial derivatives

    6. Use an optimization algorithm to minimize the cost function


### Bias

    Bias measures:

    The systematic error between the model's predicted values and the true values.
    if the difference between the model's predicted values and the true values is too big 
    then it called high bias and low variance - repersent underfitting problems

### Variance

    Variance measures:

    The model's sensitivity to variations in the training data. it repersent overfitting problems

In regularized linear regression, if the value of lambda is too large, it leads to underfitting; if it is too small, it leads to overfitting.

## Debugging a learning algorithm
    - Get more training simples -> can fix high variance
    - Reduce sets of features -> can fix high variance
    - Add more features -> can fix high bias
    - Add polynomial features -> can fix high bias too
    - decreasing lambda -> fix high bias
    - increasing lambda -> fix high variance


## Key indicators
    Precision And Recall
    Precision = TP / (TP + FP). 
For example, among all patients we predict to have a malignant tumor, it represents the percentage of those who actually have one; the higher, the better.
    
    Recall = TP / (TP + FN). 
For example, among all patients who actually have a malignant tumor, it represents the percentage of patients correctly predicted to have a malignant tumor; the higher the value, the better.

    F1 Score
    F1Score:2(PR/P+R)


### The following describes the effects of the two Support Vector Machine parameters, $C$ and $\sigma$:

$C = 1/\lambda$

A large $C$ (equivalent to a small $\lambda$) may lead to overfitting and high variance;

A small $C$ (equivalent to a large $\lambda$) may lead to underfitting and high bias;

A large $\sigma$ may lead to low variance and high bias;

A small $\sigma$ may lead to low bias and high variance.



### Recommendations for the Application of Principal Component Analysis

    A common misuse of Principal Component Analysis (PCA) is employing it solely to reduce overfitting by cutting down the number of features. This is generally a poor approach; regularization is a better alternative. The reason is that PCA discards features based on variance alone, without considering the target variable, potentially leading to the loss of crucial information. In contrast, regularization takes the target variable into account, ensuring that important data is preserved.

    Another common error is automatically including PCA as a standard step in the learning process. While this can be effective in many cases, it is usually best to start with the full set of original features and only consider PCA when necessary—such as when the algorithm runs too slowly or consumes excessive memory.



