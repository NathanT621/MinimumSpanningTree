load("NBAData.mat");
NBAstats(1,:) = [];
netRtg = NBAstats.NetRtg
reboundPct = NBAstats.REB_
turnoverRatio = NBAstats.TOV_
%%
NBAStats2(isnan(NBAStats2.GP), :) = [];
%%
ppg = NBAStats2.PTS
fgPct = NBAStats2.FG_
ftPct = NBAStats2.FT_
stlblck = NBAStats2.BLK + NBAStats2.STL
%%
% Combine Relevant Data into one matrix
stats = [ppg fgPct ftPct stlblck netRtg reboundPct turnoverRatio]
%%
% Find means and standard deviations
zppg = (ppg - mean(ppg)) ./ std(ppg, 1);
zfgPct = (fgPct - mean(fgPct)) ./ std(fgPct, 1);
zftPct = (ftPct - mean(ftPct)) ./ std(ftPct, 1);
zstlblck = (stlblck - mean(stlblck)) ./ std(stlblck, 1);
znetRtg = (netRtg - mean(netRtg)) ./ std(netRtg, 1);
zreboundPct = (reboundPct - mean(reboundPct)) ./ std(reboundPct, 1);
zturnoverRatio = (turnoverRatio - mean(turnoverRatio)) ./ std(turnoverRatio, 1);
zscores = [zppg zfgPct zftPct zstlblck znetRtg zreboundPct zturnoverRatio];
%%
% Find Euclidean Distance
n = size(zscores, 1);
D = zeros(n, n);

for i = 1:n
    for j = 1:n
        diff = zscores(i,:) - zscores(j,:);
        D(i,j) = sqrt(sum(diff.^2));
    end
end
%%
writematrix(D, "distanceMatrix.txt")