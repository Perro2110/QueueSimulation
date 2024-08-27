function B=erlangb(n,rho)
%
% Sanity check- make sure that n is a positive integer.
%
  if ((floor(n) ~= n) || (n < 1))
    warning('n is not a positive integer');
    B=NaN;
    return;
  end;
%
% Sanity check- make sure that rho >= 0.0.
%
  if (rho < 0.0)
    warning('rho is negative!');
    B=NaN;
    return;
  end;
%
% Start the recursion with B=1.
%
B=1;
%
% Run the recursion.
%
for k=1:n,
  B=((rho*B)/k)/(1+rho*B/k); 
end;