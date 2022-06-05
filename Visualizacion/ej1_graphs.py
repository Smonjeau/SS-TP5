import pandas as pd
import math
import numpy as np
import statistics as stat
import csv
import matplotlib.pyplot as plt



def readAndAdjustIntoList(filename):    
    file = open(filename)
    csvreader = csv.reader(file)
    run_raw = []
    for value in csvreader:
        run_raw.append(value.__getitem__(0))
    file.close()
    run=[]
    sum=0
    for i in range(len(run_raw)):
        if i%100==99:
            run.append(sum)
            sum=0
        else:
            sum+=int(run_raw[i])
    return run

run1=readAndAdjustIntoList("dynamic_output_0.txt")
run2=readAndAdjustIntoList("dynamic_output_1.txt")
run3=readAndAdjustIntoList("dynamic_output_2.txt")
run4=readAndAdjustIntoList("dynamic_output_3.txt")

DT=0.1 * (0.01 / 10**5)**0.5 * 0.3

xAxis=[]
for i in range(len(run1)):
    xAxis.append(DT*i*10000)

for i in range(len(run1)):
    run1[i]=run1[i]/(DT*10000)
    run2[i]=run2[i]/(DT*10000)
    run3[i]=run3[i]/(DT*10000)
    run4[i]=run4[i]/(DT*10000)
  

plt.plot(xAxis,run1,label='D=0.15')
plt.plot(xAxis,run2,label='D=0.1833')
plt.plot(xAxis,run3,label='D=0.2166')
plt.plot(xAxis,run4,label='D=0.25')
plt.legend()
plt.xlabel('Tiempo de simulacion (s)')
plt.ylabel('Flujo de particulas (m/s)')
plt.savefig('ej1_graphs.1.1.png')
plt.show()
plt.clf()


for i in range(7):
    run1.pop(0)
    run2.pop(0)
    run3.pop(0)
    run4.pop(0)

scatterx=[0.15,0.1833,0.2166,0.25]
scattery=[stat.mean(run1),stat.mean(run2),stat.mean(run3),stat.mean(run4)]
scatterError=[stat.stdev(run1),stat.stdev(run2),stat.stdev(run3),stat.stdev(run4)]

plt.errorbar(scatterx,scattery,yerr=scatterError,fmt='o')
plt.xlabel('D (m)')
plt.ylabel('Flujo de particulas (m/s)')
plt.savefig('ej1_graphs.1.2.png')
plt.show()
plt.clf()




# ===================================EJ2========================================


file = open("dynamic_input.txt")
csvreader = csv.reader(file,delimiter=' ')
particles = []
header=next(csvreader)
for value in csvreader:
    particles.append(float(value.__getitem__(2)))
file.close()
mean_radii=stat.mean(particles)

D=[0.15,0.1833,0.2166,0.25]
expected_values=scattery;

def Error(diam,k,expected):
    error=0
    for i in range(4):
        B=(300/0.3)*(9.81)**0.5
        aux=(abs(diam[i]-k*mean_radii))**1.5
        error+=(expected[i]-B*aux)**2
    return error

error=[]
LOWER=2
UPPER=5
STEP=0.01
for i in np.arange(LOWER,UPPER,STEP):
    error.append(Error(D,i,expected_values))

optimum_k=LOWER+STEP*error.index(float(min(error)))
plt.plot(np.arange(LOWER,UPPER,STEP),error)
plt.axvline(x=optimum_k, color='b', label='C='+str(optimum_k))
plt.xlabel('C')
plt.ylabel('Error')
plt.legend()
plt.savefig('ej1_graphs.2.1.png')
plt.show()
plt.clf()

# print(min(error))

# print("min error found at k="+str(optimum_k))

def f(x):
    B=(300/0.3)*(9.81)**0.5
    aux=(abs(x-optimum_k*mean_radii))**1.5
    return B*aux

print(mean_radii)

plt.errorbar(scatterx,scattery,yerr=scatterError,fmt='o')
x=np.arange(0.15,0.26,0.01)
plt.plot(x,f(x),color='red')
plt.xlabel('D (m)')
plt.ylabel('Flujo de particulas (m/s)')
plt.savefig('ej1_graphs.2.2.png')
plt.show()
plt.clf()


# ===================================EJ3========================================

data=pd.read_csv('dynamic_output_energy.txt',delimiter=',')

xAxis=[]
for i in range(0,320000,1):
    xAxis.append(i*DT)

plt.plot(xAxis,data['D0'],label='D=0.15')
plt.plot(xAxis,data['D1'],label='D=0.1833')
plt.plot(xAxis,data['D2'],label='D=0.2166')
plt.plot(xAxis,data['D3'],label='D=0.25')
plt.xlabel('Tiempo de simulacion (s)')
plt.ylabel('Energia (j)')
plt.legend()
plt.yscale('log')
plt.savefig('ej1_graphs.3.1.png')
plt.show()
plt.clf()


# ===================================EJ4========================================

data=pd.read_csv('dynamic_output_closed_energy.txt',delimiter=',')

plt.plot(xAxis,data['KT1'],label='KT=KN')
plt.plot(xAxis,data['KT2'],label='KT=2KN')
plt.plot(xAxis,data['KT3'],label='KT=3KN')
plt.xlabel('Tiempo de simulacion (s)')
plt.ylabel('Energia (j)')
plt.legend()
plt.yscale('log')
plt.savefig('ej1_graphs.4.1.png')
plt.show()
plt.clf()


run1=data['KT1'].to_list()
run2=data['KT2'].to_list()
run3=data['KT3'].to_list()


run1=run1[150000:]
run2=run2[150000:]
run3=run1[150000:]

scatterx=[10**5,2*10**5,3*10**5]
scattery=[stat.mean(run1),stat.mean(run2),stat.mean(run3)]
scatterError=[stat.stdev(run1),stat.stdev(run2),stat.stdev(run3)]

plt.errorbar(scatterx,scattery,yerr=scatterError,fmt='o')
plt.xlabel('KT')
# plt.yscale('log')
plt.ylabel('Energia Total (j)')
plt.savefig('ej1_graphs.4.2.png')
plt.show()
plt.clf()
