import { Component, OnInit } from '@angular/core';
import { BookRequest } from '../../../../services/models';
import { BookService } from '../../../../services/services';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-manage-books',
  templateUrl: './manage-books.component.html',
  styleUrl: './manage-books.component.scss'
})
export class ManageBooksComponent implements OnInit{

  bookRequest: BookRequest = {
    authorName: '',
    isbn: '',
    synopsis: '',
    title: ''
  };
  errorMessages: Array<String> = [];
  selectedPicture: string | undefined;
  selectedBookCover: any;


  constructor(private bookService: BookService, private router: Router, private activatedRoute: ActivatedRoute) {}



  ngOnInit(): void {
    // check route parameters (edit mode)
    const bookId = this.activatedRoute.snapshot.params['bookId'];
    console.log("looking fot the Book id:: " + bookId + typeof bookId);
    if(bookId){
      // get the book
      this.bookService.findBookById({ 'book-id': bookId }).subscribe({
        next: (bookResponse) => {
          // convert the response
          this.bookRequest = {
            authorName: bookResponse.author as string,
            id: bookResponse.id,
            isbn: bookResponse.isbn as string,
            shareable: bookResponse.shareable,
            synopsis: bookResponse.synopsis as string,
            title: bookResponse.title as string
          }
          // load the cover if available
          if (bookResponse.cover){
            this.selectedPicture = 'data:image/jpeg;base64,' + bookResponse.cover;
          }
        },
        error: (error) => {
          console.error('Error fetching book:', error);
        }
      })
    }
  }


  onBookCoverSelected(event: any) {
    this.selectedBookCover = event.target.files[0];
    console.log(this.selectedBookCover);

    if (this.selectedBookCover) {

      const reader = new FileReader();
      reader.onload = () => {
        this.selectedPicture = reader.result as string;
      };
      reader.readAsDataURL(this.selectedBookCover);
    }
  }

  cancel() {
    this.router.navigate(['/books/my-books']);
  }
  saveBook() {
    this.bookService.saveBook({
      connectedUser: "ANY",
      body: this.bookRequest
    }).subscribe({
      next: (bookId) => {
        console.log("Received bookId:", bookId, "Type:", typeof bookId); 
        this.bookService.uploadBookCoverPicture({
          bookId: bookId,
          body: {
            file: this.selectedBookCover,
          }
        }).subscribe({
          next: () => {
            this.router.navigate(['/books/my-books']);
          }
        });
      },
      error: (err) => {
        console.log(err.error);
        this.errorMessages = err.error.validationErrors;
      }
    });
  }
}
