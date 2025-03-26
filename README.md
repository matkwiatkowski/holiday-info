# TODO: format properly

Holiday Information Service

Decision:

Found 2 widely used solutions to get the holiday info:
- [Holiday API](https://holidayapi.com/)
- [Jollyday](https://jollyday.sourceforge.net/)

Both have some disadvantages:
- Holiday API is online only solution,
- Jollyday can have some days missing, less options for filtering.

Decided to give a configuration option to switch between the two.

Since the only performance issue might be large number of requests to Holiday API, application is designed to get 
holidays for the whole year and work on that.

Decided to drop option to filter by public/non-public holidays since Jollyday does not have this option.


