google.load('visualization', '1', {'packages':['annotatedtimeline']});
//google.setOnLoadCallback(drawGraph);

function drawGraph (values, divId) {
    var annotatedtimeline = new google.visualization.AnnotatedTimeLine(document.getElementById(divId));
    var data = new google.visualization.DataTable();
    data.addColumn('datetime', 'Date');
    data.addColumn('number', '');
    //If we have data
    if (values.length > 0) {
        data.addRows(values.length);
        for (var i=0; i < values.length; i++) {
            data.setValue(i, 0, new Date(parseInt(values[i][0]) * 1000));
            data.setValue(i, 1, parseFloat(values[i][1]));
        }
    
    
        var end = new Date(parseInt(values[values.length - 1][0]) * 1000);
        var start = new Date(parseInt(values[0][0]) * 1000);
        var yesterday = new Date(end.getTime() - 86400000); //Substract miliseconds in a day

        //If the start date is before yesterday, assign it to yesterday
        if (start.getTime() < yesterday.getTime())
            start = yesterday;

        annotatedtimeline.draw(data, {
            colors: ['green'], // The colors to be used
            displayExactValues: true, // Do not truncate values (i.e. using K suffix)
            displayRangeSelector: true, // Do not sow the range selector
            displayZoomButtons: true, // DO not display the zoom buttons
            fill: 30, // Fill the area below the lines with 30% opacity
            legendPosition: 'newRow', // Can be sameRow
            scaleColumns: [0, 1], // Have two scales, by the first and second lines
            scaleType: 'allfixed', // See docs...
            thickness: 2, // Make the lines thicker
            zoomStartTime: new Date(start.getTime()), //NOTE: month 1 = Feb (javascript to blame)
    //        zoomEndTime: new Date(end.getTime()), //NOTE: month 1 = Feb (javascript to blame)
            wmode: 'transparent'
        });
    }
    else
    {
        annotatedtimeline.draw(data, {
            colors: ['green'], // The colors to be used
            displayExactValues: true, // Do not truncate values (i.e. using K suffix)
            displayRangeSelector: true, // Do not sow the range selector
            displayZoomButtons: true, // DO not display the zoom buttons
            fill: 30, // Fill the area below the lines with 30% opacity
            legendPosition: 'newRow', // Can be sameRow
            scaleColumns: [0, 1], // Have two scales, by the first and second lines
            scaleType: 'allfixed', // See docs...
            thickness: 2, // Make the lines thicker
            zoomStartTime: new Date(), //NOTE: month 1 = Feb (javascript to blame)
            zoomEndTime: new Date(), //NOTE: month 1 = Feb (javascript to blame)
            wmode: 'transparent'
        });
    }
}