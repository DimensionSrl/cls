//
//  TodosViewController.swift
//  todos
//
//  Created by Matteo Matassoni on 04/01/2020.
//  Copyright © 2020 Dimension S.r.l. All rights reserved.
//

import UIKit
import TDSApi

fileprivate let apiHost = "http://localhost:8080"
class TodosViewController: UITableViewController {
    
    fileprivate lazy var client: CallbackClient = {
        return CallbackClient(client: Client(host: apiHost))
    }()
    
    fileprivate var todos: [TDSToDo] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        refresh(sender: self)
    }
    
    
}

// MARK: Networking Actions
fileprivate extension TodosViewController {
    
    @IBAction func refresh(sender: Any?) -> Void {
        tableView.refreshControl?.beginRefreshing()
        client.getAll(
            success: { [weak self] todos in
                self?.todos = todos
                self?.tableView.reloadData()
                self?.tableView.refreshControl?.endRefreshing()
            },
            error: { [weak self] error in
                self?.tableView.refreshControl?.endRefreshing()
                self?.handleError(error)
        })
    }
    
    func createTodo(title: String?) -> Void {
        tableView.refreshControl?.beginRefreshing()
        client.create(
            title: title ?? NSLocalizedString("Comprare il latte",
                                              comment: ""),
            success: { [weak self] todo in
                self?.todos.append(todo)
                self?.tableView.reloadData()
                self?.tableView.refreshControl?.endRefreshing()
            },
            error: { [weak self] error in
                self?.tableView.refreshControl?.endRefreshing()
                self?.handleError(error)
        })
    }
    
    @IBAction func showCreateTodo(sender: Any?) -> Void {
        let alert = UIAlertController(title: nil,
                                      message: NSLocalizedString("Nuovo todo",
                                                                 comment: ""),
                                      preferredStyle: .alert)
        alert.addTextField { textField in
            textField.placeholder = NSLocalizedString("Comprare il latte",
                                                      comment: "")
        }
        
        let confirmAction = UIAlertAction(title: NSLocalizedString("Aggiungi",
                                                                   comment: ""),
                                          style: .default) { [weak self, weak alert] _ in
                                            guard let textField = alert?.textFields?.first
                                                else { return }

                                            let todoTitle: String?
                                            if let text = textField.text, !text.isEmpty {
                                                todoTitle = text
                                            } else {
                                                todoTitle = nil
                                            }
                                            self?.createTodo(title: todoTitle)
        }
        
        alert.addAction(confirmAction)
        let cancelAction = UIAlertAction(title: NSLocalizedString("Annulla",
                                                                  comment: ""),
                                         style: .cancel,
                                         handler: nil)
        alert.addAction(cancelAction)
        
        present(alert, animated: true, completion: nil)
    }
    
}

// MARK: Errors Handling
fileprivate extension TodosViewController {
    
    func handleError(_ error: KotlinException) {
        showAlert(withErrorMessage: nil)
    }
    
    func showAlert(withErrorMessage errorMessage: String?) {
        let alertMessage = errorMessage ?? NSLocalizedString("Qualcosa è andato storto",
                                                             comment: "")
        let alert = UIAlertController(title: nil,
                                      message: alertMessage,
                                      preferredStyle: .alert)
        
        let cancelAction = UIAlertAction(title: NSLocalizedString("OK",
                                                                  comment: ""),
                                         style: .cancel,
                                         handler: nil)
        alert.addAction(cancelAction)
        
        present(alert, animated: true, completion: nil)
    }
}

// MARK: UITableViewDataSource
fileprivate let cellIdentifier = "cell"
fileprivate let dateFormatter: DateFormatter = {
    let dateFormatter = DateFormatter()
    dateFormatter.dateStyle = .short
    dateFormatter.timeStyle = .short
    return dateFormatter
}()

extension TodosViewController {
    
    override func tableView(_ tableView: UITableView,
                            numberOfRowsInSection section: Int) -> Int {
        return todos.count
    }
    
    override func tableView(_ tableView: UITableView,
                            cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier,
                                                 for: indexPath)
        
        let todo = todos[indexPath.row]
        
        if todo.done {
            let strikethroughTitle = NSAttributedString(string: todo.title,
                                                        attributes: [
                                                            NSAttributedString.Key.strikethroughStyle : NSNumber(value: NSUnderlineStyle.single.rawValue),
            ])
            cell.textLabel?.attributedText = strikethroughTitle
        } else {
            cell.textLabel?.attributedText = nil
            cell.textLabel?.text = todo.title
        }
        
        return cell
    }
}

// MARK: UITableViewDelegate
extension TodosViewController {
    
    override func tableView(_ tableView: UITableView,
                            didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
    }

    override func tableView(_ tableView: UITableView,
                            editingStyleForRowAt indexPath: IndexPath) -> UITableViewCell.EditingStyle {
        return .delete
    }

    override func tableView(_ tableView: UITableView,
                            titleForDeleteConfirmationButtonForRowAt indexPath: IndexPath) -> String? {
        return NSLocalizedString("Elimina",
                                 comment: "")
    }

    override func tableView(_ tableView: UITableView,
                            commit editingStyle: UITableViewCell.EditingStyle,
                            forRowAt indexPath: IndexPath) {
        guard editingStyle == .delete
            else { return }

        let todo = todos[indexPath.row]
        client.delete(todo: todo,
                      success: { [weak self] updatedTodo in
                        self?.todos.remove(at: indexPath.row)
                        self?.tableView.deleteRows(at: [indexPath], with: .fade)
            },
                      error: { [weak self] error in
                        self?.handleError(error)
        })
    }

    override func tableView(_ tableView: UITableView,
                            leadingSwipeActionsConfigurationForRowAt indexPath: IndexPath)
        ->   UISwipeActionsConfiguration? {

            let todo = todos[indexPath.row]

            let title = !todo.done ?
                NSLocalizedString("Completato", comment: "") :
                NSLocalizedString("Non completato", comment: "")

            let action = UIContextualAction(style: .normal,
                                            title: title,
                                            handler: { [weak self] (action, view, completionHandler) in
                                                guard let strongSelf = self
                                                    else { completionHandler(false); return }

                                                strongSelf.client.toggle(todo: todo,
                                                                         success: { [weak self] updatedTodo in
                                                                            self?.todos[indexPath.row] = updatedTodo
                                                                            self?.tableView.reloadRows(at: [indexPath],
                                                                                                       with: .fade)
                                                                            completionHandler(true)
                                                    },
                                                                         error: { [weak self] error in
                                                                            self?.handleError(error)
                                                                            completionHandler(false)
                                                })
            })

            action.image = UIImage(named: "heart")
            action.backgroundColor = !todo.done ? .green : .red
            let configuration = UISwipeActionsConfiguration(actions: [action])
            return configuration
    }
}
