function C=erlangc(n,rho)
%
% Sanity check- make sure that n is a positive integer.
%
  if ((floor(n) ~= n) || (n < 1))
    warning('n is not a positive integer');
    C=NaN;
    return;
  end;
%
% Sanity check- make sure that rho >= 0.0.
%
  if (rho < 0.0)
    warning('rho is negative!');
    C=NaN;
    return;
  end;
%
% Calculate the Erlang B probability and then convert to Erlang C.
%
B=erlangb(n,rho);
C=n*B/(n-rho*(1-B));