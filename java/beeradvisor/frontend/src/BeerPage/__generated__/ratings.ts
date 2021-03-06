

/* tslint:disable */
// This file was automatically generated and should not be edited.

// ====================================================
// GraphQL fragment: ratings
// ====================================================

export interface ratingsResult_ratings {
  id: string;  // An immutable unique Id
}

export interface ratingsResult {
  id: string;                  // Unique, immutable Id, that identifies this Beer
  ratingsResult: ratingsResult_ratings[];  // List of all Ratings for this Beer
}

//==============================================================
// START Enums and Input Objects
// All enums and input objects are included in every output file
// for now, but this will be changed soon.
// TODO: Link to issue to fix this.
//==============================================================

// 
interface AddRatingInput {
  beerId: string;
  userId: string;
  comment: string;
  stars: number;
}

//==============================================================
// END Enums and Input Objects
//==============================================================