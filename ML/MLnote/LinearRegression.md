# Linear Regression with One Variable
## There is only one input feature variable
### Gradient Descent
The idea behind gradient descent is as follows: we start by randomly selecting a combination of parameters and calculating the cost function; then, we look for the next combination of parameters that yields the greatest reduction in the cost function value. We repeat this process until we reach a local minimum.
## learning rate
It determines the size of the step we take in the direction that yields the greatest decrease in the cost function; in batch gradient descent, we simultaneously update all parameters by subtracting the product of the learning rate and the derivative of the cost function.
#### If the learning rate is too small, the result is that you can only inch along—much like a baby—in an effort to approach the lowest point; reaching the global optimum could be a very slow process.
#### If the learning rate is too high, gradient descent may overshoot the minimum point—or even fail to converge entirely. With each iteration, the algorithm takes a large step, overshooting the minimum again and again; you eventually find yourself moving further and further away from the minimum, leading to a failure to converge or even divergence.

## Feature scaling
### Ensuring that all input features have similar scales can effectively help the gradient descent algorithm converge faster.
## Mean normalization
### Divide the feature by its mean, then divide by its maximum value.
