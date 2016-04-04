%% Reading in the files and adding seizure info.
[patientData, windowData] = loadData();

%%

% Save to CSV file

csvwrite('csvlist_windowData.dat',windowData)

%% Classification
[SVM_eval, mean_sensi_test, mean_speci_test, mean_speci_train, mean_sensi_train] = SVM_epilepsy(patientData);