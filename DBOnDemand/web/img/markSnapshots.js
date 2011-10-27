zk.afterLoad('zul.db', function(){
        zul.db.Renderer.cellHTML = function (cal, y, m, day, monthofs) {
                var output = '<a href="javascript:;" style="color:grey; text-decoration: none">' + day + '</a>';
                var snapshots = eval('(' + cal.snapshots + ')');
                if(snapshots == null) return output;
                for(var i = 0; i < snapshots.length; i++) {
                        var snapshotSplited = snapshots[i].split("/");
                        var snapshotDay = parseInt(snapshotSplited[0]);
                        var snapshotMonth = parseInt(snapshotSplited[1]);
                        var snapshotYear = parseInt(snapshotSplited[2]);

                        if((y == snapshotYear) && ((m+1) == snapshotMonth) && (day == snapshotDay)) {
                                output = '<a href="javascript:;" style="color:green; font-weight: bold; text-decoration: none">' + day + '</a>';
                        }
                }
                return output;
        };
});