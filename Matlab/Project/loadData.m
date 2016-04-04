function [patientData, windowDataFinal] = loadData()
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% patientData = loadData()
% Loading the all mat files in a folder
%
% INPUT     -None.
%
% OUTPUT    -patientData, a struct containing patient data and info.
%
% Authors: Oscar Petersen, Jeppe Thaagaard and Nicolai Pedersen, MSc DTU.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

path = num2str(pwd);
if strcmp(path(1),'/')                      % Linux or Mac system
    addpath([path,'/Data'])
    fnames = dir([path,'/Data/*.mat']);
else                                        % Windows system
    addpath([path,'\Data'])
    fnames = dir([path,'\Data\*.mat']);
end

% Load files into Matlab
fname = fnames(end).name;
seizureInfo = load(fname);

% Load files into Matlab
patientData = [];
windowDataFinal = [];

for i = 0:(length(seizureInfo.SeizTimeIntoFileInSec)-1) % minus one, to account for the seizure file
    fname = fnames(i*2+1).name;
    fnameInfo = fnames(i*2+2).name;
    tempPatientData = load(fname);
    tempPatientDataInfo = load(fnameInfo);
    tempSeizureInfo = floor(seizureInfo.SeizTimeIntoFileInSec{i+1});
    patient = seizureAdd(tempPatientData.data,tempPatientDataInfo.rec_info.channel_fs(1),tempSeizureInfo);
    [features, windowData] = get_features(patient,tempPatientDataInfo.rec_info.channel_fs(1));
    
    ptLabel = ones(size(windowData,1),1);
    
    if ~isempty(strfind(fname(1:end-4),'b'))   
        windowData1 = [ptLabel*(i+1),windowData];
    else
         windowData1 = [ptLabel*(i),windowData];
    end
    fieldname = {fname(1:end-4)};
    patientData = setfield(patientData,fieldname{:},'EEG',patient); % Save patient data in structs
    patientData = setfield(patientData,fieldname{:},'features',features); % Save patient data in structs
    windowDataFinal = [windowDataFinal; windowData1];
    
end

end

