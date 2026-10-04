data = csvRead("ex1data1.txt");

x = data(:, 1);
y = data(:, 2);
m = length(y);

X = [ones(m, 1) x];

theta = zeros(2, 1);

alpha = 0.02;
iterations = 2000;

function h = hypothesis(X, theta)
    h = X * theta;
endfunction

function J = costFunction(X, y, theta)
    h = hypothesis(X, theta);
    J = (1/(2*m)) * sum((h - y).^2);
endfunction

function theta = gradientDescent(X, y, theta, alpha, iterations)
    for i = 1:iterations
        h = hypothesis(X, theta);
        error = h - y;
        gradient = (1/m) * (X' * error);
        theta = theta - alpha * gradient;
    end
endfunction

disp("Cost before = " + string(costFunction(X, y, theta)));

theta = gradientDescent(X, y, theta, alpha, iterations);

disp("theta0 = " + string(theta(1)));
disp("theta1 = " + string(theta(2)));
disp("Cost after gradient Descent = " + string(costFunction(X, y, theta)));

predict1 = [1 3.5] * theta;
predict2 = [1 7] * theta;

disp("predict1 = " + string(predict1));
disp("predict2 = " + string(predict2));

clf;
plot(x, y, 'rx');
xlabel("Population of City in 10,000s");
ylabel("Profit in $10,000s");
title("Linear Regression Fit");
plot(x, hypothesis(X, theta), 'b-');
