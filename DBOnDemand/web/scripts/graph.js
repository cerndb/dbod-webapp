google.load('visualization', '1', {'packages':['annotatedtimeline']});
//google.setOnLoadCallback(drawGraph);

function drawGraph (values, divId) {
    var annotatedtimeline = new google.visualization.AnnotatedTimeLine(document.getElementById(divId));
    //If we have data
    if (values != null) {
        var data = new google.visualization.DataTable(values);
        annotatedtimeline.draw(data, {
            colors: ['green','red'], // The colors to be used
            displayExactValues: true, // Do not truncate values (i.e. using K suffix)
            displayRangeSelector: true, // Do not sow the range selector
            displayZoomButtons: true, // DO not display the zoom buttons
            fill: 30, // Fill the area below the lines with 30% opacity
            legendPosition: 'newRow', // Can be sameRow
            scaleColumns: [0, 1], // Have two scales, by the first and second lines
            scaleType: 'allmaximized', // See docs...
            thickness: 2, // Make the lines thicker
            wmode: 'transparent'
        });
    }
    else
    {
        var data = new google.visualization.DataTable();
        data.addColumn('datetime', 'Date');
        data.addColumn('number', '');
        annotatedtimeline.draw(data, {
            colors: ['green'], // The colors to be used
            displayExactValues: true, // Do not truncate values (i.e. using K suffix)
            displayRangeSelector: true, // Do not sow the range selector
            displayZoomButtons: true, // DO not display the zoom buttons
            fill: 30, // Fill the area below the lines with 30% opacity
            legendPosition: 'newRow', // Can be sameRow
            scaleColumns: [0, 1], // Have two scales, by the first and second lines
            scaleType: 'almaximized', // See docs...
            thickness: 2, // Make the lines thicker
            zoomStartTime: new Date(), //NOTE: month 1 = Feb (javascript to blame)
            zoomEndTime: new Date(), //NOTE: month 1 = Feb (javascript to blame)
            wmode: 'transparent'
        });
    }
    google.visualization.events.addListener(annotatedtimeline, 'ready',
        function (event) {
            document.getElementById(divId).className = document.getElementById(divId).className.replace( /(?:^|\s)preloader(?!\S)/ , '' );
        });
}