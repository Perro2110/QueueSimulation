T = readtable("dati.txt");
P = readtable("parametri.txt");
Pro=readtable("probabilità.txt");

lambda = P.Var1;
mu = P.Var2;
c = P.Var3;

A = T.T_Coda;
B = T.T_Server;
C = T.T_Sistema;

rho = linspace(0,1,100);
ro=lambda/(mu*c);
indici = 1:length(A);

hold on;
%Grafico Wq Ws 
plot(indici,A,"-",Color=[0 0 1],LineWidth=1.3);
plot(indici,B,"-",Color=[1 0 0],LineWidth=1.3);
plot(indici,C,"-",Color=[0 1 0],LineWidth=1.3);
legend("W_Q_u_e_u_e","W_S_e_r_v_e_r","W_S_y_s_t_e_m");
xlabel("Pacchetti");
ylabel("Tempo");
title("Sistema A Coda M/M/C");
grid on;

figure;
xlabel("ρ");
ylabel("μW_q/μW_s");

muWq = zeros(1,100);
muWs = zeros(1,100);
Ls = zeros(1,100);
Lq = zeros(1,100);

for i = 1:100
    muWq(i) = erlangc(c,rho(i)*c)/(1-rho(i));
    muWs(i) = (erlangc(c,rho(i)*c) + c*(1-rho(i)))/(1-rho(i)); 
    Lq(i) = muWq(i)*rho(i);
    Ls(i) = Lq(i) + c*rho(i); 
end

hold on;
plot(rho,muWq,Color = [1 0 1], LineWidth=1.4);
plot(rho,muWs,Color=[0 1 1], LineWidth=1.4);
plot(ro,A(end)*mu*c,"*",Color = [0 0 0],LineWidth=3);
plot(ro,C(end)*mu*c,"*",Color = [1 0 0],LineWidth=3);
legend("μW_q","μW_s","W_q empirico","W_s empirico",Location="northwest");
grid on;

figure;
hold on;
plot(rho,Lq,Color = [1 0 1], LineWidth=1.4);
plot(rho,Ls,Color=[0 1 1], LineWidth=1.4);
legend("L_q","L_s",Location="northwest");
grid on;




s=sum((c*ro)*(0:(c-1))/(factorial(0:(c-1))));
s2=((c*ro)^c)/(factorial(c)*(1-ro));
P0=1/(s+s2);

p0=Pro.Var2(1);
figure;
hold on;
plot(Pro.Var2);
plot(1,P0,"*",Color = [0 1 0],LineWidth=3);




