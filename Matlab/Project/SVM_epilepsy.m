function [SVM_eval, mean_sensi_test, mean_speci_test, mean_speci_train, mean_sensi_train] = SVM_epilepsy(data)

fNamesPatient = fieldnames(data);
numPatients = numel(fNamesPatient);

% Preallocating Variable for classification error
TP_train = zeros(numPatients,1);
FP_train = zeros(numPatients,1);
TN_train = zeros(numPatients,1);
FN_train = zeros(numPatients,1);

TP_test = zeros(numPatients,1);
FP_test = zeros(numPatients,1);
TN_test = zeros(numPatients,1);
FN_test = zeros(numPatients,1);


sensitivity_train = zeros(numPatients,1);
sensitivity_test = zeros(numPatients,1);
specificity_train = zeros(numPatients,1);
specificity_test = zeros(numPatients,1);
FPR_train = zeros(numPatients,1);
FPR_test = zeros(numPatients,1);
SVM_eval = [];

for i = 1:numPatients
    
    fprintf('\nSVM-train %d/%d \n',i,numPatients)
    
    test_data = data.(fNamesPatient{i}).features;
    y_test = test_data(:,1); % Labels for test
    
    % Creating the training data
    train_struct = rmfield(data,fNamesPatient{i}); % removing the test set
    
    train_data = [];
    fNamesTrain = fieldnames(train_struct);
    
    for j = 1:numel(fNamesTrain)
        
        train_data = [train_data ; train_struct.(fNamesTrain{j}).features];
        
    end
    
    %%%%%% Training SVM model %%%%%%%%%%%%%%%
    
    y_train = train_data(:,1); % Labels for training
    
    options.MaxIter = 10000000;
    SVMStruct = svmtrain(train_data(:,2:end),y_train,'Options',options,'kernel_function','quadratic'); % Training
        
    y_test_est = svmclassify(SVMStruct, test_data(:,2:end)); % Classifying
    y_train_est = svmclassify(SVMStruct, train_data(:,2:end));
    
    %     y_test_est = predict(SVMStruct_full, test_data(:,2:end)); % Classifying
    %     y_train_est = predict(SVMStruct_full, train_data(:,2:end));
    
    % Compute TP, FP, FN & TN for train
    
    for e = 1:length(y_train)
        
        if y_train(e) == 1 && y_train(e) == y_train_est(e)
            TP_train(i) = TP_train(i)+1;
        elseif y_train(e) < y_train_est(e)
            FP_train(i) = FP_train(i)+1;
        elseif y_train(e) > y_train_est(e)
            FN_train(i) = FN_train(i)+1;
        else
            TN_train(i) = TN_train(i)+1;
        end
    end
    
    % Compute TP, FP, FN & TN for test
    
    for e = 1:length(y_test)
        
        if y_test(e) == 1 && y_test(e) == y_test_est(e)
            TP_test(i) = TP_test(i)+1;
        elseif y_test(e) < y_test_est(e)
            FP_test(i) = FP_test(i)+1;
        elseif y_test(e) > y_test_est(e)
            FN_test(i) = FN_test(i)+1;
        else
            TN_test(i) = TN_test(i)+1;
        end
    end
    
    
    % Sensitivity for train
    sensitivity_train(i) = TP_train(i)/(TP_train(i)+FN_train(i)); % Sensitivity, True positive rate
    specificity_train(i) = TN_train(i)/(FP_train(i)+TN_train(i));
    FPR_train(i) = 1-specificity_train(i); % False positive rate
    
    % Sensitivity for test
    sensitivity_test(i) = TP_test(i)/(TP_test(i)+FN_test(i)); % Sensitivity, True positive rate
    specificity_test(i) = TN_test(i)/(FP_test(i)+TN_test(i));
    FPR_test(i) = 1-specificity_test(i); % False positive rate
    
    evaluation = [sensitivity_train(i) specificity_train(i) FPR_train(i) sensitivity_test(i) specificity_test(i) FPR_test(i)];
    evaluation_name = {'sensitivity_train' 'specificity_train' 'FPR_train' 'sensitivity_test' 'specificity_test' 'FPR_test'};
    
    for k = 1:length(evaluation_name)
        fieldname = {fNamesPatient{i} evaluation_name{k}};
        SVM_eval = setfield(SVM_eval,fieldname{:},evaluation(k));     
    end
        
end

mean_sensi_train = mean(sensitivity_train);
mean_sensi_test = mean(sensitivity_test);

mean_speci_train = mean(specificity_train);
mean_speci_test = mean(specificity_test);

end

