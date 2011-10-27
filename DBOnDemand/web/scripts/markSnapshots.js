zk.afterLoad('zul.db', function(){
        zul.db.Renderer.cellHTML = function (cal, y, m, day, monthofs) {
                var output;
                if (monthofs == 0)
                    output = '<a href="javascript:;" style="color:Black; text-decoration: none">' + day + '</a>';
                else
                    output = '<a href="javascript:;" style="color:Grey; text-decoration: none">' + day + '</a>';
                var snapshots = eval('(' + cal.snapshots + ')');
                if(snapshots == null)
                    return output;
                for(var i = 0; i < snapshots.length; i++) {
                        var snapshotSplited = snapshots[i].split("/");
                        var snapshotDay = parseInt(snapshotSplited[0]);
                        var snapshotMonth = parseInt(snapshotSplited[1]);
                        var snapshotYear = parseInt(snapshotSplited[2]);

                        if((y == snapshotYear) && ((m+1) == (snapshotMonth)) && (day == snapshotDay)) {
                            if (monthofs == 0)
                                output = '<a href="javascript:;" style="color:LimeGreen; font-weight: bold; text-decoration: none">' + day + '</a>';
                            else
                                output = '<a href="javascript:;" style="color:PaleGreen; font-weight: bold; text-decoration: none">' + day + '</a>';
                        }
                }
                return output;
        };
});