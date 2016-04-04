function patient = seizureAdd(patientData,fs,seizureInfo)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% patient = seizureAdd(tempPatientData,fs,seizureInfo)
% Adding ones if there is a seizure or zeros if there is no seizure.
%
% INPUT     -patientData.
%           -fs, sampling frequency.
%           -seizureInfo, info about the seizure.
%
% OUTPUT    -patient, patient data with seizure info.
%
% Authors: Oscar Petersen, Jeppe Thaagaard and Nicolai Pedersen, MSc DTU.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Pre-allocating space for labels
labels = zeros(length(patientData),1);

% Checking if there is no seizures
if isempty(seizureInfo) == 1
    patient = [patientData labels];
else
    
    seizureInfo(all(diff(seizureInfo,1,2)==0,2),:)=[];
    
    ptSeizure = seizureInfo*fs; 
    
    if ptSeizure(1,1) == 0
        ptSeizure(1,1) = 1;
    end
    
    
    for i = 1:size(ptSeizure,1)
        labels(ptSeizure(i,1):ptSeizure(i,2)) = 1;
    end
    
    patient = [labels patientData];
    
end

end

