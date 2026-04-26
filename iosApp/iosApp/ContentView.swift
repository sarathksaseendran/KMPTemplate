import SwiftUI
import shared

struct ContentView: View {
    @StateObject var authViewModel = ObservableAuthViewModel()

    var body: some View {
        Group {
            if authViewModel.isLoggedIn {
                HomeView(viewModel: authViewModel)
            } else {
                LoginView(viewModel: authViewModel)
            }
        }
    }
}

struct LoginView: View {
    @ObservedObject var viewModel: ObservableAuthViewModel
    @State private var username = "emilys"
    @State private var password = "emilyspass"

    var body: some View {
        VStack(spacing: 20) {
            Text("Welcome Back")
                .font(.largeTitle)
                .bold()
            
            TextField("Username", text: $username)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding(.horizontal)
            
            SecureField("Password", text: $password)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding(.horizontal)
            
            if let error = viewModel.errorMessage {
                Text(error)
                    .foregroundColor(.red)
                    .font(.caption)
            }
            
            Button(action: {
                viewModel.login(username: username, password: password)
            }) {
                if viewModel.isLoading {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                } else {
                    Text("Login")
                        .bold()
                }
            }
            .frame(maxWidth: .infinity)
            .padding()
            .background(Color.blue)
            .foregroundColor(.white)
            .cornerRadius(10)
            .padding(.horizontal)
            .disabled(viewModel.isLoading)
        }
    }
}

struct HomeView: View {
    @ObservedObject var viewModel: ObservableAuthViewModel
    
    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                if let user = viewModel.loggedInUser {
                    AsyncImage(url: URL(string: user.image)) { image in
                        image.resizable()
                            .aspectRatio(contentMode: .fill)
                    } placeholder: {
                        ProgressView()
                    }
                    .frame(width: 120, height: 120)
                    .clipShape(Circle())
                    
                    Text("Welcome, \(user.firstName) \(user.lastName)!")
                        .font(.title2)
                        .bold()
                    
                    Text("@\(user.username)")
                        .foregroundColor(.secondary)
                }
                
                Spacer()
                
                Button("Logout") {
                    viewModel.logout()
                }
                .foregroundColor(.red)
            }
            .padding()
            .navigationTitle("Home")
        }
    }
}

class ObservableAuthViewModel: ObservableObject {
    @Published var isLoggedIn = false
    @Published var isLoading = false
    @Published var errorMessage: String? = nil
    @Published var loggedInUser: User? = nil
    
    private let viewModel: AuthViewModel = KoinHelper().getAuthViewModel()
    private var cancellables = [Any]()
    
    init() {
        self.loggedInUser = viewModel.getLoggedInUser()
        
        let isLoggedInAdapter = KoinHelperKt.asAdapter(viewModel.isLoggedIn)
        let uiStateAdapter = KoinHelperKt.asAdapter(viewModel.uiState)

        cancellables.append(
            isLoggedInAdapter.subscribe(
                onEach: { [weak self] (loggedIn: AnyObject) in
                    if let loggedInBool = loggedIn as? Bool {
                        DispatchQueue.main.async {
                            self?.isLoggedIn = loggedInBool
                            if loggedInBool {
                                self?.loggedInUser = self?.viewModel.getLoggedInUser()
                            } else {
                                self?.loggedInUser = nil
                            }
                        }
                    }
                },
                onComplete: {},
                onThrow: { _ in }
            )
        )
        
        cancellables.append(
            uiStateAdapter.subscribe(
                onEach: { [weak self] (state: AnyObject) in
                    if let state = state as? AuthUiState {
                        DispatchQueue.main.async {
                            self?.isLoading = state is AuthUiStateLoading
                            if let error = state as? AuthUiStateError {
                                self?.errorMessage = error.message
                            } else {
                                self?.errorMessage = nil
                            }
                        }
                    }
                },
                onComplete: {},
                onThrow: { _ in }
            )
        )
    }
    
    func login(username: String, password: String) {
        viewModel.login(username: username, password: password)
    }
    
    func logout() {
        viewModel.logout()
    }
    
    deinit {
        // ViewModel is cleared automatically or when container is destroyed
    }
}
