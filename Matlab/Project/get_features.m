function [features_allWindows, windowData] = get_features( data , fs)

% X = Normalized features
% y = Associated labels

% %% ---=== Windowing ===---
label = data(:,1);
% data = data(:,2:end);
data = data(:,6) - data(:,2);


[row,col] = size(data);

% 4 seconds of EEG
window_size = fs*2;

% Overlap
window_size80 = floor(window_size*0.5);

% Pre-allocating space
window = zeros(window_size, col);

% Size of seizure array (+1 for the first window, which is substracted)
num_windows = (ceil((row-window_size)/(window_size80)))+1; % Number of windows

% Number of features
number_of_features = 3;

% Data for each window stored for use in android studio
windowData = [];

% Pre-allocating space for feature array
features_temp = zeros(1, number_of_features);

% Variable array for gathering the features from each window
features_allWindows = [];
% http://se.mathworks.com/help/stats/pdf.html#bulrrtu-2

% Defining and setting up the zero crossing detector
Objectzerocross = dsp.ZeroCrossingDetector;

k = window_size; % First step size

% If window is larger than dataset
if k > row
    k = row;
end


while k <= row
    
    % Creating window
    window = data(k-window_size+1:k,:);
    
    features_vec = [];
    
    % Features for each channel
    for m = 1:col
        
%         % 1.Mean
%         features_temp(1,1) = mean(window(:,m));
%         
%         % 2.Var
         features_temp(1,2) = var(window(:,m));
%         
%         % 3. Max-Min
%         features_temp(1,3) = abs(max(window(:,m))-min(window(:,m)));
%         
%         % 4.Zero-crossing
%         % Count zero crossing in input
%         features_temp(1,4) = step(Objectzerocross,window(:,m));
        
        % 5. Shannon entropy
        p = abs(window(:,m))/sum(abs(window(:,m)));
        features_temp(1) = sum(-((p+eps).*(log2(p+eps))));
        
        % 6. Bandpower of delta-band (0.5 - 4 Hz)
        features_temp(2) = bandpower(window(:,m),fs,[0.5 4]);
        %mean(spec(delta).*conj(spec(delta)));
        
        
        features_vec = [features_vec features_temp];
        
    end
    
        % Label window
        check = sum(label(k-window_size+1:k,1));
    
        if check > window_size/4
            features_vec = [1 features_vec];
            
        else
            features_vec = [0 features_vec];
        end
    
    % Each window is saved for use in Android studio
    
    windowData = [windowData window]; 
    
    % Array of all features for each window (For supervised training)
    features_allWindows = [features_allWindows ; features_vec];
    
    
    if k > row-window_size80 && k < row % To get the last set of data point analyzed
        window_size = row-k;
        k = row;
        release(Objectzerocross); % Released because of change of data size
        if window_size < 20 % 20 samples
            k = row + 1;
            num_windows = num_windows-1;
        end
    else
        k = k + window_size80; % Overlap
    end
    
    
end

features_allWindows(:,2:end) = zscore(features_allWindows(:,2:end));


windowData = [features_allWindows(:,1)' ; windowData]';

end

